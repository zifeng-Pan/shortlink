package org.personalproj.shortlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.resp
 * @Author: PzF
 * @CreateTime: 2024-03-03  14:37
 * @Description: 短链接创建响应DTO
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkCreateRespDTO {

    /**
     *
     * 分组id
     */
    private String gid;


    /**
     *
     * 原始链接
     */
    private String originUrl;

    /**
     *
     * 完整短链接
     */
    private String fullShortUrl;
}
