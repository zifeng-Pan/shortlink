package org.personalproj.shortlink.admin.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.config
 * @Author: PzF
 * @CreateTime: 2024-01-28  16:29
 * @Description: 利用Redisson中的布隆过滤器防止缓存穿透
 * @Version: 1.0
 */
@Configuration
public class RBloomFilterConfiguration {
    /**
     *
     * 用户昵称布隆过滤器
     */
    @Bean
    public RBloomFilter<String> userNickNameCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(100000000, 0.001);
        return cachePenetrationBloomFilter;
    }
}
