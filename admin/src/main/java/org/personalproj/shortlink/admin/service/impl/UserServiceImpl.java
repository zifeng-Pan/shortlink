package org.personalproj.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.common.constnat.RedisCacheConstant;
import org.personalproj.shortlink.admin.common.convention.exception.ClientException;
import org.personalproj.shortlink.admin.common.enums.UserErrorCode;
import org.personalproj.shortlink.admin.dao.entity.UserDO;
import org.personalproj.shortlink.admin.dao.mapper.UserMapper;
import org.personalproj.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.personalproj.shortlink.admin.dto.resp.UserActualRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserRespDTO;
import org.personalproj.shortlink.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

    private final RedissonClient redissonClient;

    @Override
    public UserRespDTO getUserByUserName(String username) {
        UserDO queryResult = query().eq("username", username).one();
        if (queryResult == null) {
            throw new ClientException(UserErrorCode.USER_NULL);
        }
        return BeanUtil.copyProperties(queryResult, UserRespDTO.class);
    }

    @Override
    public UserActualRespDTO getUserActualInfoByUserName(String username){
        UserDO queryResult = query().eq("username", username).one();
        if(queryResult == null){
            throw new ClientException(UserErrorCode.USER_NULL);
        }
        return BeanUtil.copyProperties(queryResult, UserActualRespDTO.class);
    }

    @Override
    public Boolean hasUserName(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO userRegisterReqDTO) {
        if(hasUserName(userRegisterReqDTO.getUsername())){
           throw new ClientException(UserErrorCode.USER_NAME_EXIST);
        }
        UserDO userDO = BeanUtil.copyProperties(userRegisterReqDTO, UserDO.class);
        // 对于大量的恶意相同用户名的注册，每一次注册先拿到对应的锁，没有拿到就不允许相同用户名注册,抛出用户已经存在的异常。
        // 即对于大量相同用户名注册，防止全部进行数据库操作，用分布式锁进行串行化
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER + userDO.getUsername());
        try {
            if(lock.tryLock()){
                boolean success = save(userDO);
                if(!success){
                    throw new ClientException(UserErrorCode.USER_REGISTER_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(userDO.getUsername());
            }
            throw  new ClientException(UserErrorCode.USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }


    }
}




