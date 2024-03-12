package org.personalproj.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.personalproj.shortlink.common.convention.exception.ServerException;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDO;
import org.personalproj.shortlink.project.dao.entity.ShortLinkRouteDO;
import org.personalproj.shortlink.project.dao.mapper.ShortLinkMapper;
import org.personalproj.shortlink.project.dao.mapper.ShortLinkRouteMapper;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleBinRecoverReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleBinRemoveReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkRecycleBinPageRespDTO;
import org.personalproj.shortlink.project.service.RecycleBinService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static org.personalproj.shortlink.common.constnat.RedisCacheConstant.ROUTE_SHORT_LINK_KEY;
import static org.personalproj.shortlink.common.constnat.RedisCacheConstant.ROUTE_SHORT_LINK_NULL_KEY;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.service.impl
 * @Author: PzF
 * @CreateTime: 2024-03-11  12:31
 * @Description: 短链接回收站服务实现类
 * @Version: 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService{

    private final StringRedisTemplate stringRedisTemplate;

    private final ShortLinkRouteMapper shortLinkRouteMapper;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void shortLinkRecycle(ShortLinkRecycleReqDTO shortLinkRecycleReqDTO) {
        // 短链接删除,由于是逻辑删除，不更改路由表
        LambdaUpdateWrapper<ShortLinkDO> recycleWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, shortLinkRecycleReqDTO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, shortLinkRecycleReqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag,0)
                .set(ShortLinkDO::getDelFlag, 1);
        int deleteSuccess = baseMapper.update(null, recycleWrapper);
        if(deleteSuccess == -1){
            throw new ServerException("短链接回收失败");
        }
        stringRedisTemplate.opsForValue().set(String.format(ROUTE_SHORT_LINK_NULL_KEY, shortLinkRecycleReqDTO.getFullShortUrl()),"-", 30, TimeUnit.SECONDS);
        stringRedisTemplate.delete(String.format(ROUTE_SHORT_LINK_KEY, shortLinkRecycleReqDTO.getFullShortUrl()));
    }

    @Override
    public IPage<ShortLinkRecycleBinPageRespDTO> pageQuery(ShortLinkRecycleBinPageReqDTO shortLinkRecycleBinPageReqDTO) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid,shortLinkRecycleBinPageReqDTO.getGidList())
                .eq(ShortLinkDO::getDelFlag, 1)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage =  baseMapper.selectPage(shortLinkRecycleBinPageReqDTO, queryWrapper);
        return resultPage.convert(row -> BeanUtil.toBean(row, ShortLinkRecycleBinPageRespDTO.class));
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void recover(ShortLinkRecycleBinRecoverReqDTO shortLinkRecycleBinRecoverReqDTO) {
        LambdaUpdateWrapper<ShortLinkDO> recoverWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, shortLinkRecycleBinRecoverReqDTO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, shortLinkRecycleBinRecoverReqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag,1)
                .set(ShortLinkDO::getDelFlag, 0);
        int recoverSuccess = baseMapper.update(null, recoverWrapper);
        if(recoverSuccess == -1){
            throw new ServerException("短链接恢复失败");
        }
        stringRedisTemplate.delete(String.format(ROUTE_SHORT_LINK_NULL_KEY, shortLinkRecycleBinRecoverReqDTO.getFullShortUrl()));;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void shortLinkRemove(ShortLinkRecycleBinRemoveReqDTO shortLinkRecycleBinRemoveReqDTO) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, shortLinkRecycleBinRemoveReqDTO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, shortLinkRecycleBinRemoveReqDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag,1);

        LambdaQueryWrapper<ShortLinkRouteDO> shortLinkRouteQueryWrapper = Wrappers.lambdaQuery(ShortLinkRouteDO.class)
                .eq(ShortLinkRouteDO::getGid, shortLinkRecycleBinRemoveReqDTO.getGid())
                .eq(ShortLinkRouteDO::getFullShortUrl, shortLinkRecycleBinRemoveReqDTO.getFullShortUrl());

        int shortLinkRemoveSuccess = baseMapper.delete(queryWrapper);
        if(shortLinkRemoveSuccess == -1 || shortLinkRemoveSuccess == 0 ){
            throw new ServerException("服务端彻底移除短链接失败");
        }
        int shortLinkRouteRemoveSuccess = shortLinkRouteMapper.delete(shortLinkRouteQueryWrapper);
        if(shortLinkRouteRemoveSuccess == -1){
            throw new ServerException("服务端短链接路由移除失败");
        }
    }
}
