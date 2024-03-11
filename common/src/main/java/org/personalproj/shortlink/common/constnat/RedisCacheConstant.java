package org.personalproj.shortlink.common.constnat;

import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.common.constant
 * @Author: PzF
 * @CreateTime: 2024-03-03  09:36
 * @Description: Redis缓存常量
 * @Version: 1.0
 */
public class RedisCacheConstant {

    public static final String LOCK_USER_REGISTER = "short-link:user-register:";

    public static final String LOCK_SHORT_LINK_CREATE = "short-link:create:";

    public static final String USER_COUNT_KEY = "short-link:user-count:";

    public static final String USER_LOGIN_KEY = "short-link:user-login:";

    public static final Long USER_LOGIN_TIMEOUT = 30L;

    public static final TimeUnit USER_LOGIN_TIMEUNIT = TimeUnit.MINUTES;

    /**
     * 短链接跳转key
     */
    public static final String ROUTE_SHORT_LINK_KEY = "short_link_route_to_%s";

    /**
     *
     * 短链接路由锁，目的是在缓存不存在full_short_url对应的origin_url时，限制一个线程去查询数据库并保存在缓存中，减少数据库操作
     */
    public static final String LOCK_ROUTE_SHORT_LINK_KEY = "lock_short_link_route_to_%s";

    /**
     *
     * 短链接跳转空值key
     */
    public static final String ROUTE_SHORT_LINK_NULL_KEY = "short_link_route_null_to_%s";

    /**
     *
     * 永久短链接默认有效期时间
     */
    public static final long DEFAULT_SHORT_LINK_CACHE_VALID_TIME = 2629800000L;

}
