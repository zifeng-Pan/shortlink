package org.personalproj.shortlink.admin.dao.entity;


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

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.dao.entity
 * @Author: PzF
 * @CreateTime: 2024-01-31  13:26
 * @Description: 短链接分组实体
 * @Version: 1.0
 */
@Data
@TableName("t_group")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDO extends BaseDO implements Serializable {
    @TableField(exist = false)
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}
