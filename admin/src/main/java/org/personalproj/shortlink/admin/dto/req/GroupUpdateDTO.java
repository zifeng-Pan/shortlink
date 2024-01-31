package org.personalproj.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.dto.req
 * @Author: PzF
 * @CreateTime: 2024-01-31  16:26
 * @Description: 短链接组更新DTO
 * @Version: 1.0
 */
@Data
public class GroupUpdateDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}
