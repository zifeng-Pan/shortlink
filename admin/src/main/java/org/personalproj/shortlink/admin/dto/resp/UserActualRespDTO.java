package org.personalproj.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.dto.resp
 * @Author: PzF
 * @CreateTime: 2024-01-28  14:18
 * @Description: 用户真实信息返回
 * @Version: 1.0
 */

@Data
public class UserActualRespDTO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     *
     * 用户昵称
     */
    private String nickname;


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
