package org.personalproj.shortlink.admin.dto.resp;

import lombok.Data;

/**
 *@BelongsProject: shortlink
 *@BelongsPackage: org.personalproj.shortlink.admin.dto.resp
 *@Author: PzF
 *@CreateTime: 2024-01-27  14:39
 *@Description: 用户返回响应实体
 *@Version: 1.0
 */
@Data
public class UserRespDTO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;


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
