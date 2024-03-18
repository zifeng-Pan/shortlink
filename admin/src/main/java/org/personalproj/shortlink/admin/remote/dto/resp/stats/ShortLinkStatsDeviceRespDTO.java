package org.personalproj.shortlink.admin.remote.dto.resp.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.resp.stats
 * @Author: PzF
 * @CreateTime: 2024-03-15  09:03
 * @Description: 短链接访问设备类型详情响应页
 * @Version: 1.0
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsDeviceRespDTO {
    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 访问设备
     */
    private String device;

    /**
     * 占比
     */
    private Double ratio;
}
