package org.personalproj.shortlink.admin.remote.dto.req;

import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-03  14:32
 * @Description: 短链接创建DTO
 * @Version: 1.0
 */
@Data
public class ShortLinkCreateReqDTO {

    /**
     * 所属短链接组的gid
     */
    private String gid;

    /**
     * 域名
     */
    private String domain;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 创建类型 0：接口创建 1：控制台创建
     */
    private Integer createType;

    /**
     * 有效期类型 0：永久有效， 1：自定义有效期时间
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 短链接相关描述
     */
    private String description;

    /**
     *
     * 短链接相关图标
     */
    private String favicon;
}
