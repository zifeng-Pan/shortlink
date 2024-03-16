package org.personalproj.shortlink.project.dto.resp.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.resp.stats
 * @Author: PzF
 * @CreateTime: 2024-03-15  08:57
 * @Description: 短链接高频访问IP响应实体
 * @Version: 1.0
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsTopIpRespDTO {
    /**
     * 统计
     */
    private Integer cnt;

    /**
     * IP
     */
    private String ip;
}
