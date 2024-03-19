package org.personalproj.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkStatisticDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsAccessRecordGroupReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.personalproj.shortlink.project.dto.resp.stats.ShortLinkStatsAccessRecordGroupRespDTO;
import org.personalproj.shortlink.project.dto.resp.stats.ShortLinkStatsAccessRecordRespDTO;
import org.personalproj.shortlink.project.dto.resp.stats.ShortLinkStatsRespDTO;

public interface ShortLinkStatisticService extends IService<ShortLinkStatisticDO> {

    /**
     *
     * 根据请求的短链接以及起止时间返回短链接的统计监控数据，呈现的是限定时间段内单个短链接相关指标数据
     */
    ShortLinkStatsRespDTO singleShortLinkStatistic(ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 获取限定时间段内，用户某一个短链接分组内所有的短链接监控指标数据
     *
     * @param requestParam 获取分组短链接监控数据入参
     * @return 分组短链接监控数据
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    /**
     *
     * 短链接访问记录查询，呈现的是限定时间段内单个短链接的访问记录（以条目的形式呈现）
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO shortLinkStatsAccessRecordReqDTO);

    /**
     *
     * 短链接组内指定时间访问记录监控数据
     */
    IPage<ShortLinkStatsAccessRecordGroupRespDTO> shortLinkStatsAccessRecordGroup(ShortLinkStatsAccessRecordGroupReqDTO shortLinkStatsAccessRecordGroupReqDTO);
}
