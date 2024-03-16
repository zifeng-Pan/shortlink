package org.personalproj.shortlink.project.dto.req;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-14  18:43
 * @Description: 单个短链接监控统计请求实体
 * @Version: 1.0
 */

@Data
public class ShortLinkStatsReqDTO {

    /**
     *
     * 要获取的统计数据的短链接
     */
    private String fullShortUrl;

    /**
     *
     * 短链接的gid
     */
    private String gid;

    /**
     *
     * 监控数据的开始日期
     */
    private String startDate;

    /**
     *
     * 监控数据的结束日期
     */
    private String endDate;
}
