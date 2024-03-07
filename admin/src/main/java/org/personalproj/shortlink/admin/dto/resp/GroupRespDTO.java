package org.personalproj.shortlink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.dto.resp
 * @Author: PzF
 * @CreateTime: 2024-01-31  15:30
 * @Description: 短链接分组响应DTO
 * @Version: 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRespDTO {

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

    /**
     *
     * 当前分组下短链接数量统计
     */
    private Integer shortLinkCount;
}
