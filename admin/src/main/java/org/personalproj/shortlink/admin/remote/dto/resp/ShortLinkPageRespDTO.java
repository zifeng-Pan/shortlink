package org.personalproj.shortlink.admin.remote.dto.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.resp
 * @Author: PzF
 * @CreateTime: 2024-03-06  17:14
 * @Description: 短链接分页响应参数
 * @Version: 1.0
 */
@Data
public class ShortLinkPageRespDTO {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属短链接组的gid
     */
    private String gid;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 短链接访问量
     */
    private Integer clickNum;

    /**
     * 启用标识 0：启用 1：未启用
     */
    private Integer enableStatus;

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
     * 短链接创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     *
     * 短链接更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     *
     * 短链接相关图标
     */
    private String favicon;

}
