package org.personalproj.shortlink.project.dto.resp;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.resp
 * @Author: PzF
 * @CreateTime: 2024-03-07  09:54
 * @Description: 查询分组下短链接数目响应实体
 * @Version: 1.0
 */

@Data
public class ShortLinkCountQueryRespDTO {

    /**
     * 短链接分组标识
     */
    private String gid;

    /**
     *
     * 短链接分组下短链接数目
     */
    private Integer shortLinkCount;

}
