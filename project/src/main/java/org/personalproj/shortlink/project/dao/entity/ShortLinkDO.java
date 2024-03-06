package org.personalproj.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.personalproj.shortlink.common.database.BaseDO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dao.entity
 * @Author: PzF
 * @CreateTime: 2024-03-03  13:34
 * @Description: 短链接实体
 * @Version: 1.0
 */
@TableName(value ="t_link")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ShortLinkDO extends BaseDO implements Serializable{
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

    @TableField(exist = false)
    @Serial
    private static final long serialVersionUID = 1L;
}