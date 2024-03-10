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

    public static final String LOCK_ROUTE_SHORT_LINK_KEY = "lock_short_link_route_to_%s";

}
