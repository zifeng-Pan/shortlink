package org.personalproj.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.personalproj.shortlink.common.convention.exception.ServerException;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDO;
import org.personalproj.shortlink.project.dao.mapper.ShortLinkMapper;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleReqDTO;
import org.personalproj.shortlink.project.service.RecycleBinService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.personalproj.shortlink.common.constnat.RedisCacheConstant.ROUTE_SHORT_LINK_KEY;

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
        stringRedisTemplate.delete(String.format(ROUTE_SHORT_LINK_KEY, shortLinkRecycleReqDTO.getFullShortUrl()));
    }
}
