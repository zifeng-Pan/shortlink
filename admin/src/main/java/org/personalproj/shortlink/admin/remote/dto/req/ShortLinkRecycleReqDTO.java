package org.personalproj.shortlink.admin.remote.dto.req;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.remote.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-11  12:23
 * @Description: 短链接回收请求DTO
 * @Version: 1.0
 */
@Data
public class ShortLinkRecycleReqDTO {

    /**
     *
     * 短链接回收分组标识
     */
    private String gid;

    /**
     *
     * 短链接回收完整短链接
     */
    private String fullShortUrl;

}
