package org.personalproj.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.dto.req
 * @Author: PzF
 * @CreateTime: 2024-01-28  16:42
 * @Description: 用户注册请求传输对象
 * @Version: 1.0
 */

@Data
public class UserRegisterReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 用户邮箱
     */
    private String mail;
}
