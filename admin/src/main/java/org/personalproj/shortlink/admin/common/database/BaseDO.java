package org.personalproj.shortlink.admin.common.database;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.common.database
 * @Author: PzF
 * @CreateTime: 2024-01-31  15:21
 * @Description: 数据库基础属性DO
 * @Version: 1.0
 */

@Data
public class BaseDO {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 信息更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 逻辑删除标志，0：未删除，1：删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
}
