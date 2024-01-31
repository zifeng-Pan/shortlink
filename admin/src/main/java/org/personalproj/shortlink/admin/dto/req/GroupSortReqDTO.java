package org.personalproj.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.dto.req
 * @Author: PzF
 * @CreateTime: 2024-01-31  18:49
 * @Description: 短链接分组排序order更新
 * @Version: 1.0
 */
@Data
public class GroupSortReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private Integer sortOrder;
}
