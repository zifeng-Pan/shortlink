package org.personalproj.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkStatisticDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.personalproj.shortlink.project.dto.resp.stats.ShortLinkStatsRespDTO;

public interface ShortLinkStatisticService extends IService<ShortLinkStatisticDO> {

    /**
     *
     * 根据请求的短链接以及起止时间返回短链接的统计监控数据
     */
    ShortLinkStatsRespDTO singleShortLinkStatistic(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 获取分组内所有的短链接监控数据
     *
     * @param requestParam 获取分组短链接监控数据入参
     * @return 分组短链接监控数据
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);
}
