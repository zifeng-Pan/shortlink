package org.personalproj.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.dto.req
 * @Author: PzF
 * @CreateTime: 2024-01-30  14:52
 * @Description: 用户登录请求DTO
 * @Version: 1.0
 */

@Data
public class UserLoginReqDTO {

    /**
     *
     * 用户登录用户名
     */
    private String username;


    /**
     *
     * 用户登录密码
     */
    private String password;

}
