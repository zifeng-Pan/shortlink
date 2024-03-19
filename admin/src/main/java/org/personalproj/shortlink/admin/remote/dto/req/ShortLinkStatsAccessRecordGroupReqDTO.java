package org.personalproj.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-19  15:07
 * @Description: 短链接组访问记录请求实体,主要用于访问记录的分页查询
 * @Version: 1.0
 */
@Data
public class ShortLinkStatsAccessRecordGroupReqDTO extends Page {

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
