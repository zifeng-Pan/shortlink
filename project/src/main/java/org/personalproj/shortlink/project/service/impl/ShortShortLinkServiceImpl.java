package org.personalproj.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.personalproj.shortlink.common.convention.exception.ClientException;
import org.personalproj.shortlink.common.convention.exception.ServerException;
import org.personalproj.shortlink.project.common.enums.ValidDateType;
import org.personalproj.shortlink.project.dao.entity.*;
import org.personalproj.shortlink.project.dao.mapper.*;
import org.personalproj.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.personalproj.shortlink.project.service.ShortLinkService;
import org.personalproj.shortlink.project.toolkit.LinkUtil;
import org.personalproj.shortlink.project.toolkit.ShortLinkHashUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.personalproj.shortlink.common.constnat.RedisCacheConstant.*;

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

    private final StringRedisTemplate stringRedisTemplate;

    private final ShortLinkStatisticMapper shortLinkStatisticMapper;

    private final ShortLinkOsStatisticMapper shortLinkOsStatisticMapper;

    private final ShortLinkLocationStatisticMapper shortLinkLocationStatisticMapper;

    private final ShortLinkBrowserStatisticMapper shortLinkBrowserStatisticMapper;

    private final ShortLinkAccessLogsMapper shortLinkAccessLogsMapper;

    @Value("${short-link.statistic.location.user-key}")
    private String mapUserKey;

    private final AtomicBoolean uvFirstFlag = new AtomicBoolean();

    private final AtomicReference<String> uv = new AtomicReference<>();

    @Override
    public void restoreUrl(String shortUri, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InterruptedException, IOException {
        // 对路由表设置的是fullShortUrl（全局唯一）与gid之间的映射，不直接fullShortUrl映射shortUri的原因是需要到短链接表中看是否删除以及是否启用,同时还需要看是否过期
        String serverName = httpServletRequest.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(ROUTE_SHORT_LINK_KEY, fullShortUrl));
        // 如果为空，可能缓存失效或者现在没有对应的缓存数据，可能存在缓存击穿或者缓存穿透的问题
        if(StrUtil.isNotBlank(originalLink)){
            try {
                shortLinkStatistic(fullShortUrl,null,httpServletRequest,httpServletResponse);
                httpServletResponse.sendRedirect(originalLink);
                return;
            } catch (IOException e) {
                throw new ClientException(String.format("短链接:{},跳转失败",fullShortUrl));
            }
        }
        // redis不存在full_short_url这个key的时候，解决缓存穿透[缓存中没有对应的缓存数据，如果是缓存失效的话，布隆过滤器中还是存在的]的问题
        // 1. 检查布隆过滤器中是否存在full_short_url,如果不存在直接返回空
        if(!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)){
            httpServletResponse.sendRedirect("/page/notfound");
            throw new ClientException(String.format("短链接:{},未创建",fullShortUrl));
        }
        // 2. 判定为存在的情况，检查redis缓存是否存储了对应的空值，如果有的话直接返回空
        String nullCacheValue = stringRedisTemplate.opsForValue().get(String.format(ROUTE_SHORT_LINK_NULL_KEY, fullShortUrl));
        if(StrUtil.isNotBlank(nullCacheValue)){
            httpServletResponse.sendRedirect("/page/notfound");
            throw new ClientException(String.format("短链接:{},跳转失败",fullShortUrl));
        }
        // 3. 空值key不存在于缓存中的话，加锁查询数据库，数据库中查询不到的时候，将空值key插入redis中
        RLock lock = redissonClient.getLock(String.format(LOCK_ROUTE_SHORT_LINK_KEY, fullShortUrl));
        if(lock.tryLock()) {
            try {
                // 双重判定锁
                originalLink = stringRedisTemplate.opsForValue().get(String.format(ROUTE_SHORT_LINK_KEY, fullShortUrl));
                if (StrUtil.isNotBlank(originalLink)) {
                    shortLinkStatistic(fullShortUrl,null,httpServletRequest,httpServletResponse);
                    httpServletResponse.sendRedirect(originalLink);
                    return;
                }
                LambdaQueryWrapper<ShortLinkRouteDO> shortLinkRouteLambdaQueryWrapper = Wrappers.lambdaQuery(ShortLinkRouteDO.class)
                        .eq(ShortLinkRouteDO::getFullShortUrl, fullShortUrl);
                ShortLinkRouteDO shortLinkRouteDO = shortLinkRouteMapper.selectOne(shortLinkRouteLambdaQueryWrapper);
                if (shortLinkRouteDO == null) {
                    // TODO: 严格来说这里需要进行风控
                    stringRedisTemplate.opsForValue().set(String.format(ROUTE_SHORT_LINK_NULL_KEY, fullShortUrl),"-", 30, TimeUnit.SECONDS);
                    httpServletResponse.sendRedirect("/page/notfound");
                    return;
                }
                LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                        .eq(ShortLinkDO::getGid, shortLinkRouteDO.getGid())
                        .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                        .eq(ShortLinkDO::getDelFlag, 0)
                        .eq(ShortLinkDO::getEnableStatus, 0);
                ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
                if (shortLinkDO != null) {
                    if (shortLinkDO.getValidDateType() == 1){
                        Date validDate = shortLinkDO.getValidDate();
                        if(DateTime.now().isAfter(validDate)) {
                            stringRedisTemplate.opsForValue().set(String.format(ROUTE_SHORT_LINK_NULL_KEY, fullShortUrl),"-", 30, TimeUnit.SECONDS);
                            httpServletResponse.sendRedirect("/page/notfound");
                            throw new ClientException("短链接已过期");
                        }
                        stringRedisTemplate.opsForValue().set(
                                String.format(ROUTE_SHORT_LINK_KEY, fullShortUrl),
                                shortLinkDO.getOriginUrl(),
                                getShortLinkCacheValidTime(validDate),
                                TimeUnit.MILLISECONDS
                        );
                    } else {
                        stringRedisTemplate.opsForValue().set(
                                String.format(ROUTE_SHORT_LINK_KEY, fullShortUrl),
                                shortLinkDO.getOriginUrl(),
                                getShortLinkCacheValidTime(null),
                                TimeUnit.MILLISECONDS
                        );
                    }
                    shortLinkStatistic(fullShortUrl,shortLinkDO.getGid(),httpServletRequest,httpServletResponse);
                    httpServletResponse.sendRedirect(shortLinkDO.getOriginUrl());
                }
            } catch (IOException e) {
                throw new ClientException("短链接跳转失败");
            } finally {
                lock.unlock();
            }
        } else{
            // 多线程情况下，等待获取锁期间休眠一段时间后重试
            Thread.sleep(50);
            restoreUrl(shortUri,httpServletRequest,httpServletResponse);
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
        shortLink.setFavicon(getFavicon(shortLinkCreateReqDTO.getOriginUrl()));

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
                    // 缓存预热
                    stringRedisTemplate.opsForValue().set(
                                String.format(ROUTE_SHORT_LINK_KEY,shortLinkRouteDO.getFullShortUrl()),
                                shortLink.getOriginUrl(),
                                getShortLinkCacheValidTime(shortLinkCreateReqDTO.getValidDate()),
                                TimeUnit.MILLISECONDS
                            );
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
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
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
                .eq(ShortLinkDO::getId, id)
                .eq(ShortLinkDO::getDelFlag,1);
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

    /**
     *
     * 根据短链接创建请求生成短链接后缀
     */
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


    private void shortLinkStatistic(String fullShortUrl, String gid, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        Cookie[] cookies = httpServletRequest.getCookies();
        FutureTask<AtomicBoolean> generateCookieTask = null;
        String browser = LinkUtil.getBrowser(httpServletRequest);
        String os = LinkUtil.getOperatingSystem(httpServletRequest);
        // 如果cookie是空的
        if(ArrayUtil.isEmpty(cookies)){
            generateCookieTask = new FutureTask<>(new generateCookieToResponse(fullShortUrl, httpServletResponse), uvFirstFlag);
            Thread addCookieThread = new Thread(generateCookieTask);
            addCookieThread.start();
        } else {
            Arrays.stream(cookies)
                    .filter(cookie -> Objects.equals(cookie.getName(),"uv"))
                    .findFirst()
                    .map(cookie -> cookie.getValue())
                    .ifPresentOrElse( cookie -> {
                        Long uvAdd = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATISTIC_COOKIE_UV + fullShortUrl, cookie);
                        if( uvAdd != null && uvAdd > 0L){
                            uvFirstFlag.set(Boolean.FALSE);
                            uv.set(cookie);
                        }
                        uvFirstFlag.set(uvAdd != null && uvAdd > 0L);
                    },
                    new generateCookieToResponse(fullShortUrl, httpServletResponse));
        }
        if(StrUtil.isBlank(gid)){
            LambdaQueryWrapper<ShortLinkRouteDO> shortLinkRouteLambdaQueryWrapper = Wrappers.lambdaQuery(ShortLinkRouteDO.class)
                    .eq(ShortLinkRouteDO::getFullShortUrl, fullShortUrl);
            ShortLinkRouteDO shortLinkRouteDO = shortLinkRouteMapper.selectOne(shortLinkRouteLambdaQueryWrapper);
            gid = shortLinkRouteDO.getGid();
        }
        Date now = new Date();
        int weekDay = DateUtil.dayOfWeekEnum(now).getIso8601Value();
        // uip 标记设置
        String remoteAddr = LinkUtil.getActualIp(httpServletRequest);
        Long uipAdd = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATISTIC_UIP + fullShortUrl, remoteAddr);
        boolean uipFirstFlag = (uipAdd != null && uipAdd > 0L);

        ShortLinkStatisticDO shortLinkStatistic;
        try {
            shortLinkStatistic = ShortLinkStatisticDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .date(now)
                    .pv(1)
                    .uv((generateCookieTask == null ? uvFirstFlag.get() : generateCookieTask.get().get()) ? 1 : 0)
                    .uip(uipFirstFlag ? 1 : 0)
                    .hour(DateUtil.hour(now, true))
                    .weekday(weekDay)
                    .build();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new ServerException("异步生成cookie任务被打断:" + e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new ServerException("异步生成cookie任务执行失败:" + e.getMessage());
        }

        // 短链接统计表相关实体创建
        // 短链接访问操作系统统计
        ShortLinkOsStatisticDO shortLinkOsStatisticDO = ShortLinkOsStatisticDO.builder()
                .os(os)
                .gid(gid)
                .fullShortUrl(fullShortUrl)
                .date(now)
                .cnt(1)
                .build();
        // 短链接访问浏览器统计
        ShortLinkBrowserStatisticDO shortLinkBrowserStatisticDO = ShortLinkBrowserStatisticDO.builder()
                .browser(browser)
                .cnt(1)
                .gid(gid)
                .fullShortUrl(fullShortUrl)
                .date(now)
                .build();
        // 短链接访问日志记录
        ShortLinkAccessLogsDO shortLinkAccessLogsDO = ShortLinkAccessLogsDO.builder()
                .browser(browser)
                .os(os)
                .gid(gid)
                .fullShortUrl(fullShortUrl)
                .ip(remoteAddr)
                .user(uv.get())
                .build();

        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            shortLinkStatisticMapper.shortLinkStatisticInsert(shortLinkStatistic);
            generateLocationStatisticDO(fullShortUrl,gid, remoteAddr, now);
            shortLinkOsStatisticMapper.shortLinkOsState(shortLinkOsStatisticDO);
            shortLinkBrowserStatisticMapper.shortLinkBrowserState(shortLinkBrowserStatisticDO);
            shortLinkAccessLogsMapper.insert(shortLinkAccessLogsDO);
            transactionManager.commit(transactionStatus);
        } catch (Exception e){
            transactionManager.rollback(transactionStatus);
            log.error("gid:{},短链接:{} 对应的访问统计量新增或更新失败",gid,fullShortUrl);
            throw new ServerException(e.getMessage());
        }
    }

    /**
     *
     * 短链接地区信息统计
     */
    private void generateLocationStatisticDO(String fullShortUrl, String gid, String remoteAddr, Date now){
        ShortLinkLocationStatisticDO locationStatisticDO;
        // 向高德API发送请求获取IP相关的地区信息
        Map<String, Object> mapRequestMap = new HashMap<>(2);
        mapRequestMap.put("key", mapUserKey);
        mapRequestMap.put("ip",remoteAddr);
        String mapApiResponse = HttpUtil.get("https://restapi.amap.com/v3", mapRequestMap);
        JSONObject jsonObject = JSON.parseObject(mapApiResponse);
        String infoCode = jsonObject.getString("infocode");
        if(StrUtil.isNotBlank(infoCode) &&  Objects.equals(infoCode,"10000")){
            String province = jsonObject.getString("province");
            boolean unKnownFlag = StrUtil.isBlank(province);
            locationStatisticDO = ShortLinkLocationStatisticDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .cnt(1)
                    .country("中国")
                    .province(unKnownFlag ? "未知" : province)
                    .city(unKnownFlag ? "未知" : jsonObject.getString("city"))
                    .adcode(unKnownFlag ? "未知" : jsonObject.getString("adcode"))
                    .build();
            shortLinkLocationStatisticMapper.shortLinkLocaleState(locationStatisticDO);
        }
    }


    /**
     *
     * 生成UV统计时需要的的cookie的任务
     */
    @Data
    @AllArgsConstructor
    private class generateCookieToResponse implements Runnable{

        private String fullShortUrl;

        private HttpServletResponse response;

        @Override
        public void run() {
            String uvCookieValue = UUID.fastUUID().toString(false);
            Cookie uvCookie = new Cookie("uv", uvCookieValue);
            uvCookie.setMaxAge(60 * 60 * 24 * 30);
            uvCookie.setPath(StrUtil.sub(fullShortUrl,fullShortUrl.indexOf("/"),fullShortUrl.length()));
            response.addCookie(uvCookie);
            uv.set(uvCookie.getValue());
            stringRedisTemplate.opsForSet().add(SHORT_LINK_STATISTIC_COOKIE_UV + fullShortUrl, uvCookieValue);
            uvFirstFlag.set(Boolean.TRUE);
        }
    }

    /**
     *
     * 获取短链接的有效时间
     */
    private long getShortLinkCacheValidTime(Date validDate){
        return Optional.ofNullable(validDate)
                .map(time -> DateUtil.between(new Date(), time, DateUnit.MS))
                .orElse(DEFAULT_SHORT_LINK_CACHE_VALID_TIME);
    }

    /**
     *
     * 根据Url获取网站图标
     */
    @SneakyThrows
    private String getFavicon(String url) {
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                return faviconLink.attr("abs:href");
            }
        }
        return null;
    }
}