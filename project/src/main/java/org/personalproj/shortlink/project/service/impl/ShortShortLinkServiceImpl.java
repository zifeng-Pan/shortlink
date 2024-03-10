package org.personalproj.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.personalproj.shortlink.common.convention.exception.ClientException;
import org.personalproj.shortlink.common.convention.exception.ServerException;
import org.personalproj.shortlink.project.common.enums.ValidDateType;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDO;
import org.personalproj.shortlink.project.dao.entity.ShortLinkRouteDO;
import org.personalproj.shortlink.project.dao.mapper.ShortLinkMapper;
import org.personalproj.shortlink.project.dao.mapper.ShortLinkRouteMapper;
import org.personalproj.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.personalproj.shortlink.project.service.ShortLinkService;
import org.personalproj.shortlink.project.toolkit.ShortLinkHashUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.personalproj.shortlink.common.constnat.RedisCacheConstant.LOCK_SHORT_LINK_CREATE;

/**
* @author panzifeng
* @description 针对表【t_link】的数据库操作Service实现
* @createDate 2024-03-03 13:33:45
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    private final PlatformTransactionManager transactionManager;

    private final ShortLinkRouteMapper shortLinkRouteMapper;

    @Override
    public void restoreUrl(String shortUri, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // 对路由表设置的是fullShortUrl（全局唯一）与gid之间的映射，不直接fullShortUrl映射shortUri的原因是需要到短链接表中看是否删除以及是否启用,同时还需要看是否过期
        String serverName = httpServletRequest.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        LambdaQueryWrapper<ShortLinkRouteDO> shortLinkRouteLambdaQueryWrapper = Wrappers.lambdaQuery(ShortLinkRouteDO.class)
                .eq(ShortLinkRouteDO::getFullShortUrl, fullShortUrl);
        ShortLinkRouteDO shortLinkRouteDO = shortLinkRouteMapper.selectOne(shortLinkRouteLambdaQueryWrapper);
        if(shortLinkRouteDO == null){
            // TODO: 严格来说这里需要进行封控
            return;
        }
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, shortLinkRouteDO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
        if(shortLinkDO != null){
            if(shortLinkDO.getValidDateType() == 1 && DateTime.now().isAfter(shortLinkDO.getValidDate())){
                throw new ClientException("短链接已过期");
            }
            try {
                httpServletResponse.sendRedirect(shortLinkDO.getOriginUrl());
            } catch (IOException e) {
                throw new ServerException("跳转原始连接失败");
            }
        }
    }

    @Override
    public ShortLinkCreateRespDTO create(ShortLinkCreateReqDTO shortLinkCreateReqDTO) {
        // 事务控制数据库保存以及布隆过滤器的原子性
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 短链接生成
        String shortLinkSuffix = generateSuffix(shortLinkCreateReqDTO);

        ShortLinkDO shortLink = BeanUtil.toBean(shortLinkCreateReqDTO, ShortLinkDO.class);
        String fullShortUrl = shortLinkCreateReqDTO.getDomain() + "/" + shortLinkSuffix;
        shortLink.setFullShortUrl(fullShortUrl);
        shortLink.setShortUri(shortLinkSuffix);
        shortLink.setEnableStatus(0);

        ShortLinkRouteDO shortLinkRouteDO = ShortLinkRouteDO.builder()
                .gid(shortLink.getGid())
                .fullShortUrl(shortLink.getFullShortUrl())
                .build();

        // 通过锁来限制同时多个请求产生的并发问题（概率比较小），加锁的目的是在后端层面限制对于重复的完整短链接，只添加一次，减少和数据库交互
        RLock lock = redissonClient.getLock(LOCK_SHORT_LINK_CREATE + shortLink.getFullShortUrl());

        try{
            if (lock.tryLock()){
                // 编程式事务管理，让save和加入布隆过滤器放在一个事务中，事务保证保存数据库以及布隆过滤器添加的原子性【防止一些异常导致数据库插入成功但是没有加入到布隆过滤器中】
                TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
                try {
                    save(shortLink);
                    // gid与full_short_url路由关系表数据的插入
                    shortLinkRouteMapper.insert(shortLinkRouteDO);
                    // 插入数据库成功之后，再对布隆过滤器进行设置
                    shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
                    // 事务提交
                    transactionManager.commit(transactionStatus);
                } catch (Exception e){
                    // 事务回滚
                    transactionManager.rollback(transactionStatus);
                    log.error("保存短链接进入数据库以及布隆过滤器过程中出现错误，重新检查代码");
                    throw e;
                }
            } else {
                log.warn("originURL:{}，对应的相同短链接：{}已经在生成，重复请求过多",shortLink.getFullShortUrl(),fullShortUrl);
            }
        } finally {
            lock.unlock();
        }

        String protocol = shortLinkCreateReqDTO.getProtocol();
        String returnFullShortUrl = (protocol == null ? shortLink.getFullShortUrl(): protocol + "//" + shortLink.getFullShortUrl());
        return ShortLinkCreateRespDTO
                .builder()
                .fullShortUrl(returnFullShortUrl)
                .gid(shortLink.getGid())
                .originUrl(shortLink.getOriginUrl())
                .build();


    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageQuery(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, shortLinkPageReqDTO.getGid())
                .eq(ShortLinkDO::getDelFlag, 0);
        IPage<ShortLinkDO> resultPage =  baseMapper.selectPage(shortLinkPageReqDTO, queryWrapper);
        return resultPage.convert(row -> BeanUtil.toBean(row, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkCountQueryRespDTO> shortLinkCountQuery(List<String> gidList) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid,count(*) as shortLinkCount")
                .in("gid", gidList)
                .eq("del_flag", 0)
                .groupBy("gid");
        List<Map<String,Object>> shortLinkCountMaps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkCountMaps, ShortLinkCountQueryRespDTO.class);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void shortLinkUpdate(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO) {
        // 这里设置域名不能修改，否则短链接域名下唯一的规则很可能出现问题
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, shortLinkUpdateReqDTO.getGid())
                .eq(ShortLinkDO::getId, shortLinkUpdateReqDTO.getId())
                .eq(ShortLinkDO::getDelFlag, "0")
                .eq(ShortLinkDO::getEnableStatus, "0")
                .set(Objects.equals(shortLinkUpdateReqDTO.getValidDateType(), ValidDateType.PERMANENT.getValidStatueCode()), ShortLinkDO::getValidDate, null);
        shortLinkUpdateReqDTO.setGid(null);
        shortLinkUpdateReqDTO.setId(null);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(shortLinkUpdateReqDTO, ShortLinkDO.class);
        int updateSuccess = baseMapper.update(shortLinkDO, updateWrapper);
        if(updateSuccess == -1){
            throw new ServerException("短链接更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void shortLinkChangeGroup(String oldGid, Long id, String gid) {
        // 更新Gid的时候同时更新短链接路由表(注意路由表的分片键是full_short_url),所以更新短链接路由表的时候不需要删除对应的数据,只更改gid
        // TODO: 这里更新组别效率是非常低的，由于数据量比较大，我们进行了分表处理，此时的分片键是gid，
        //  当我们要修改gid的时候，为了使得之后的查询效率不要变低，我们应该的做法是删除原数据，将gid修改后插入新表（因为很可能新的gid会分配到新表）
        //  但是这种效率非常低，可以后期考虑使用消息队列来优化（异步操作）
        LambdaQueryWrapper<ShortLinkDO> shortLinkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, oldGid)
                .eq(ShortLinkDO::getId, id);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(shortLinkQueryWrapper);
        shortLinkDO.setGid(gid);

        LambdaUpdateWrapper<ShortLinkRouteDO> shortLinkRouteLambdaUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkRouteDO.class)
                .eq(ShortLinkRouteDO::getFullShortUrl, shortLinkDO.getFullShortUrl())
                .set(ShortLinkRouteDO::getGid, gid);
        // 路由表的更新
        int shortLinkRouteUpdateSuccess = shortLinkRouteMapper.update(null, shortLinkRouteLambdaUpdateWrapper);
        if(shortLinkRouteUpdateSuccess == -1){
            throw new ServerException("短链接路由表更新失败");
        }

        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 转换为Date类型
        Date currentDate = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
        // 设置更新日期
        shortLinkDO.setUpdateTime(currentDate);
        int deleteSuccess = baseMapper.delete(shortLinkQueryWrapper);

        if(deleteSuccess == - 1){
            throw new ServerException("短链接更换组别(删除旧数据)失败");
        }
        int insertSuccess = baseMapper.insert(shortLinkDO);
        if(insertSuccess == -1){
            throw new ServerException("短链接更换组别(插入新数据)失败");
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void shortLinkDelete(String gid, Long id) {
        // 短链接删除,由于是逻辑删除，不更改路由表
        LambdaUpdateWrapper<ShortLinkDO> deleteWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getId, id)
                .set(ShortLinkDO::getDelFlag, 1);
        int deleteSuccess = baseMapper.update(null, deleteWrapper);
        if(deleteSuccess == -1){
            throw new ServerException("短链接删除失败");
        }
    }

    private String generateSuffix(ShortLinkCreateReqDTO shortLinkCreateReqDTO){
        String originUrl = shortLinkCreateReqDTO.getOriginUrl();
        String shortUri;
        int generateRetryCount = 0;
        while(true){
            if(generateRetryCount > ShortLinkHashUtil.GENERATE_RETRY_TIMES){
                throw new ServerException("短链接频繁生成重试，请稍后再试");
            }

            shortUri = ShortLinkHashUtil.hash2Base62String(originUrl + UUID.randomUUID());
            // 保证域名下短链接唯一即可，不需要保证全局唯一
            // 布隆过滤器的误判主要是两个不同的值经过哈希函数计算得到了相同的哈希值，比如这里两个不同的短链接可能得到同一个哈希值从而判定为短链接重复
            // 这里的循环解决的就是这种误判的情况
            if(!shortUriCreateCachePenetrationBloomFilter.contains(shortLinkCreateReqDTO.getDomain() + "/"  + shortUri)){
                break;
            }
            generateRetryCount++;
        }
        return shortUri;
    }
}