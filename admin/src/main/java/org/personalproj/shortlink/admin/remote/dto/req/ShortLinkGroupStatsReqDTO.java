package org.personalproj.shortlink.admin.remote.dto.req;

import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.remote.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-16  12:32
 * @Description: 短链接分组统计请求实体
 * @Version: 1.0
 */
@Data
public class ShortLinkGroupStatsReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
