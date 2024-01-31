package org.personalproj.shortlink.admin.common.constnat;

import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.common.constnat
 * @Author: PzF
 * @CreateTime: 2024-01-30  09:36
 * @Description: Redis缓存常量
 * @Version: 1.0
 */
public class RedisCacheConstant {

    public static final String LOCK_USER_REGISTER = "short-link:user-register:";

    public static final String USER_COUNT_KEY = "short-link:user-count:";

    public static final String USER_LOGIN_KEY = "short-link:user-login:";

    public static final Long USER_LOGIN_TIMEOUT = 30L;

    public static final TimeUnit USER_LOGIN_TIMEUNIT = TimeUnit.MINUTES;

}
