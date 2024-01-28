package org.personalproj.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.dao.entity.UserDO;
import org.personalproj.shortlink.admin.dao.mapper.UserMapper;
import org.personalproj.shortlink.admin.dto.resp.UserActualRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserRespDTO;
import org.personalproj.shortlink.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

/**
* @author panzifeng
* @description 针对表【t_user】的数据库操作Service实现
* @createDate 2024-01-27 12:55:52
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>
    implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    @Override
    public UserRespDTO getUserByUserName(String username) {
        UserDO queryResult = query().eq("username", username).one();
        return BeanUtil.copyProperties(queryResult, UserRespDTO.class);
    }

    @Override
    public UserActualRespDTO getUserActualInfoByUserName(String username){
        return BeanUtil.copyProperties(query().eq("username",username).one(), UserActualRespDTO.class);
    }

    @Override
    public Boolean hasUserName(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }
}




