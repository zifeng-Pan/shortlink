package org.personalproj.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.personalproj.shortlink.common.convention.exception.ServerException;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDO;
import org.personalproj.shortlink.project.dao.mapper.ShortLinkMapper;
import org.personalproj.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkPageReqDTO;
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
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Map;

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

        // 通过锁来限制同时多个请求产生的并发问题（概率比较小），加锁的目的是在后端层面限制对于重复的完整短链接，只添加一次，减少和数据库交互
        RLock lock = redissonClient.getLock(LOCK_SHORT_LINK_CREATE + shortLink.getFullShortUrl());

        try{
            if (lock.tryLock()){
                // 编程式事务管理，让save和加入布隆过滤器放在一个事务中，事务保证保存数据库以及布隆过滤器添加的原子性【防止一些异常导致数据库插入成功但是没有加入到布隆过滤器中】
                TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
                try {
                    save(shortLink);
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

        return ShortLinkCreateRespDTO
                .builder()
                .fullShortUrl(shortLink.getFullShortUrl())
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




