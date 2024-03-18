package org.personalproj.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkAccessLogsDO;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-14  18:43
 * @Description: 短链接访问记录请求实体,主要用于访问记录的分页查询
 * @Version: 1.0
 */

@Data
public class ShortLinkStatsAccessRecordReqDTO extends Page<ShortLinkAccessLogsDO> {

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
