package org.personalproj.shortlink.admin.common.enums;

import org.personalproj.shortlink.admin.common.convention.errorcode.IStatusCode;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.common.enums
 * @Author: PzF
 * @CreateTime: 2024-01-31  14:58
 * @Description: 用户短链接组管理相关错误码
 * @Version: 1.0
 */
public enum GroupErrorCode implements IStatusCode {
    // ========== 二级宏观错误码 用户短链接组相关错误 ==========
    GROUP_SAVE_ERROR("B000300","短链接组添加失败");


    private final String code;

    private final String message;

    GroupErrorCode(String code, String message) {
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
