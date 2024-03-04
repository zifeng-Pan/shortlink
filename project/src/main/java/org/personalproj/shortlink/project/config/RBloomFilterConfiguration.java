package org.personalproj.shortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.config
 * @Author: PzF
 * @CreateTime: 2024-03-03  21:11
 * @Description: 利用Redisson中的布隆过滤器检查短链接是否重复
 * @Version: 1.0
 */
@Configuration
public class RBloomFilterConfiguration {
    /**
     *
     * 用户昵称布隆过滤器
     */
    @Bean
    public RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("shortUriCreateCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(100000000, 0.001);
        return cachePenetrationBloomFilter;
    }
}
