package org.personalproj.shortlink.admin.remote.dto.req;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.remote.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-11  16:17
 * @Description: 短链接回收站彻底删除请求DTO
 * @Version: 1.0
 */

@Data
public class ShortLinkRecycleBinRemoveReqDTO {
    /**
     *
     * 短链接分组标识
     */
    private String gid;

    /**
     *
     * 完整短链接
     */
    private String fullShortUrl;
}
