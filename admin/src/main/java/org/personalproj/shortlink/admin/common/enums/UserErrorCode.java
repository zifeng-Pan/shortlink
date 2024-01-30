package org.personalproj.shortlink.admin.common.enums;

import org.personalproj.shortlink.admin.common.convention.errorcode.IStatusCode;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.common.convention.errorcode
 * @Author: PzF
 * @CreateTime: 2024-01-27  16:09
 * @Description: 枚举用户错误验证码
 * @Version: 1.0
 */
public enum UserErrorCode implements IStatusCode {
    // ========== 二级宏观错误码 用户相关错误 ==========
    USER_NULL("B000200","用户记录不存在"),

    USER_NAME_EXIST("B000201","用户名存在"),

    USER_EXIST("B000202","用户存在"),

    USER_REGISTER_ERROR("B000203", "新增用户记录失败");


    private final String code;

    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
