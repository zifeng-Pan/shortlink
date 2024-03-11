package org.personalproj.shortlink.project.dto.req;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-11  15:55
 * @Description: 回收站短链接恢复
 * @Version: 1.0
 */
@Data
public class ShortLinkRecycleBinRecoverReqDTO {

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
