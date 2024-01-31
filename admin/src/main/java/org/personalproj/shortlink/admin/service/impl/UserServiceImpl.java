package org.personalproj.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.common.constnat.RedisCacheConstant;
import org.personalproj.shortlink.admin.common.convention.exception.ClientException;
import org.personalproj.shortlink.admin.common.enums.UserErrorCode;
import org.personalproj.shortlink.admin.dao.entity.UserDO;
import org.personalproj.shortlink.admin.dao.mapper.UserMapper;
import org.personalproj.shortlink.admin.dto.req.UserLoginReqDTO;
import org.personalproj.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.personalproj.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.personalproj.shortlink.admin.dto.resp.UserActualRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserRespDTO;
import org.personalproj.shortlink.admin.service.UserService;
import org.personalproj.shortlink.admin.toolkit.JWTUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.personalproj.shortlink.admin.common.constnat.RedisCacheConstant.*;
import static org.personalproj.shortlink.admin.common.enums.UserErrorCode.USER_LOGIN_OUT_ERROR;

/**
 * @author panzifeng
 * @description 针对表【t_user】的数据库操作Service实现
 * @createDate 2024-01-27 12:55:52
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>
        implements UserService {

    private final RBloomFilter<String> userNickNameCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;


    @Override
    public UserRespDTO getUserByUserName(String username) {
        UserDO queryResult = query()
                .eq("username", username)
                .eq("del_flag", 0).one();
        if (queryResult == null) {
            throw new ClientException(UserErrorCode.USER_NULL);
        }
        return BeanUtil.copyProperties(queryResult, UserRespDTO.class);
    }

    @Override
    public UserActualRespDTO getUserActualInfoByUserName(String username) {
        UserDO queryResult = query()
                .eq("username", username)
                .eq("del_flag", 0).one();
        if (queryResult == null) {
            throw new ClientException(UserErrorCode.USER_NULL);
        }
        return BeanUtil.copyProperties(queryResult, UserActualRespDTO.class);
    }

    @Override
    public Boolean hasNickName(String nickName) {
        return userNickNameCachePenetrationBloomFilter.contains(nickName);
    }

    private String generateUserName() {
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(USER_COUNT_KEY))) {
            stringRedisTemplate.opsForValue().set(USER_COUNT_KEY, "0");
        }
        Long userCount = stringRedisTemplate.opsForValue().increment(USER_COUNT_KEY);
        if (userCount / 1000000000L > 0) {
            return UUID.fastUUID().toString(false).substring(0, 9);
        } else {
            return UUID.fastUUID().toString(false).substring(0, 8);
        }

    }


    @Override
    public Boolean isLogin(String token) {
        return stringRedisTemplate.hasKey(USER_LOGIN_KEY + token);
    }


    @Override
    public void logOut(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        String userLoginKey = USER_LOGIN_KEY + token;
        Boolean delete = stringRedisTemplate.delete(userLoginKey);
        if (Boolean.FALSE.equals(delete)) {
            throw new ClientException(USER_LOGIN_OUT_ERROR);
        }

    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO) {
        // step1: 判断用户的用户名密码是否正确(去数据库中比对)，注意这里也需要限流，防止恶意的大批量的用户进行登录
        UserDO userDO = query()
                .eq("username", userLoginReqDTO.getUsername())
                .eq("password", userLoginReqDTO.getPassword())
                .eq("del_flag", 0).one();
        if (userDO == null) {
            throw new ClientException("用户名或者密码错误");
        }

        // step2: 生成token以及userLoginRespDTO
        String token = JWTUtil.generateToken(userLoginReqDTO);
        UserLoginRespDTO userLoginRespDTO = BeanUtil.copyProperties(userDO, UserLoginRespDTO.class);
        userLoginRespDTO.setToken(token);

        // step3: 将存在的用户存入redis缓存中，
        stringRedisTemplate.opsForValue().set(USER_LOGIN_KEY + token, JSONUtil.toJsonStr(userLoginRespDTO), USER_LOGIN_TIMEOUT, USER_LOGIN_TIMEUNIT);
        return userLoginRespDTO;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public String register(UserRegisterReqDTO userRegisterReqDTO) {
        // 前端调用hasUserName接口防止重复
        UserDO userDO = BeanUtil.copyProperties(userRegisterReqDTO, UserDO.class);

        // 对于大量的恶意相同用户名的注册，每一次注册先拿到对应的锁，没有拿到就不允许相同用户名注册,抛出用户已经存在的异常。
        // 即对于大量相同用户名注册，防止全部进行数据库操作，用分布式锁进行串行化
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER + userDO.getPhone());
        String username = generateUserName();
        userDO.setUsername(username);
        try {
            if (lock.tryLock()) {
                boolean success = save(userDO);
                if (!success) {
                    throw new ClientException(UserErrorCode.USER_REGISTER_ERROR);
                }

                userNickNameCachePenetrationBloomFilter.add(userDO.getNickname());
            } else {
                throw new ClientException(UserErrorCode.USER_NAME_EXIST);
            }
            return username;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void update(UserUpdateReqDTO userUpdateReqDTO) {
        // Step1 : 新的昵称不能够存在
        if (hasNickName(userUpdateReqDTO.getNickname())) {
            throw new ClientException(UserErrorCode.USER_NAME_EXIST);
        }
        // Step2: 从UpdateDTO复制属性到userDo中并进行数据库更新
        UserDO userDO = BeanUtil.copyProperties(userUpdateReqDTO, UserDO.class);
        boolean success = update(userDO, Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, userUpdateReqDTO.getUsername())
                .eq(UserDO::getDelFlag, 0));
        // 更新失败则抛出异常，更新成功则需要在布隆过滤器里面加上更新后的userName
        if (!success) {
            throw new ClientException(UserErrorCode.USER_UPDATE_ERROR);
        }
        userNickNameCachePenetrationBloomFilter.add(userUpdateReqDTO.getNickname());

    }
}




