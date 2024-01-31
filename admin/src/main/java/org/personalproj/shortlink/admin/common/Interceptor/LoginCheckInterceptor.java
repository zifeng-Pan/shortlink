package org.personalproj.shortlink.admin.common.Interceptor;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.personalproj.shortlink.admin.common.convention.exception.ClientException;
import org.personalproj.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.personalproj.shortlink.admin.toolkit.JWTUtil;
import org.personalproj.shortlink.admin.toolkit.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.personalproj.shortlink.admin.common.constnat.RedisCacheConstant.USER_LOGIN_KEY;
import static org.personalproj.shortlink.admin.common.enums.UserErrorCode.*;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.common.Interceptor
 * @Author: PzF
 * @CreateTime: 2024-01-30  22:16
 * @Description: 用户登录状态检查拦截器
 * @Version: 1.0
 */
public class LoginCheckInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public LoginCheckInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        String userLoginKey = USER_LOGIN_KEY + token;

        // 如果redis缓存中没有对应的json字符串，抛出用户未登录异常
        String jsonStr = stringRedisTemplate.opsForValue().get(userLoginKey);
        if(StringUtils.isBlank(jsonStr)){

            throw new ClientException(USER_NOT_LOGIN_ERROR);
        }


        // token有效性验证
        if(JWTUtil.tokenVerify(token)){
            stringRedisTemplate.delete(userLoginKey);
            response.setStatus(401);
            throw new ClientException(USER_TOKEN_VALIDATE_ERROR);
        }

        // 既没有过期，token也有效，则取出并放入userHolder中
        UserLoginRespDTO user = JSONUtil.toBean(jsonStr, UserLoginRespDTO.class);
        UserHolder.saveUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
