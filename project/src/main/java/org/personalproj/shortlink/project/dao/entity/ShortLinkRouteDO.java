package org.personalproj.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.personalproj.shortlink.common.database.BaseDO;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dao.entity
 * @Author: PzF
 * @CreateTime: 2024-03-10  20:15
 * @Description: 完整短链接与gid之间的路由表
 * @Version: 1.0
 */

@TableName(value ="t_link_route")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ShortLinkRouteDO extends BaseDO {
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
     * 完整短链接
     */
    private String fullShortUrl;
}
