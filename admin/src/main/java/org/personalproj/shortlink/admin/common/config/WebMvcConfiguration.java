package org.personalproj.shortlink.admin.common.config;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.common.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.common.config
 * @Author: PzF
 * @CreateTime: 2024-01-30  20:16
 * @Description: Web相关配置
 * @Version: 1.0
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {


    private final StringRedisTemplate stringRedisTemplate;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginCheckInterceptor loginCheckInterceptor = new LoginCheckInterceptor(stringRedisTemplate);
        InterceptorRegistration interceptors = registry.addInterceptor(loginCheckInterceptor);
        interceptors.excludePathPatterns(
                "/api/shortlink/admin/v1/user/common/**"
                ).order(1);
    }
}
