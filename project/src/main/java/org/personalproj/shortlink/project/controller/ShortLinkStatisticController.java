package org.personalproj.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.common.convention.result.Result;
import org.personalproj.shortlink.common.convention.result.Results;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsAccessRecordGroupReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.personalproj.shortlink.project.dto.resp.stats.ShortLinkStatsAccessRecordGroupRespDTO;
import org.personalproj.shortlink.project.dto.resp.stats.ShortLinkStatsAccessRecordRespDTO;
import org.personalproj.shortlink.project.dto.resp.stats.ShortLinkStatsRespDTO;
import org.personalproj.shortlink.project.service.ShortLinkStatisticService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.controller
 * @Author: PzF
 * @CreateTime: 2024-03-14  18:35
 * @Description: 短链接统计监控控制层
 * @Version: 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/v1/stats")
public class ShortLinkStatisticController {

    private final ShortLinkStatisticService shortLinkStatisticService;

    /**
     *
     * 单个短链接指定时间内统计指标【指标数据】
     */
    @PostMapping("/single")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(@RequestBody ShortLinkStatsReqDTO shortLinkStatsReqDTO){
        return Results.success(shortLinkStatisticService.singleShortLinkStatistic(shortLinkStatsReqDTO));
    }

    /**
     *
     * 短链接组指定时间内指标统计数据【指标数据】
     */
    @PostMapping("/group")
    public Result<ShortLinkStatsRespDTO> shortLinkGroupStats(@RequestBody ShortLinkGroupStatsReqDTO ShortLinkGroupStatsReqDTO){
        return Results.success(shortLinkStatisticService.groupShortLinkStats(ShortLinkGroupStatsReqDTO));
    }

    /**
     *
     * 单个短链接指定时间内访问记录监控数据【log数据】
     */
    @PostMapping("/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkAccessRecordStats(@RequestBody ShortLinkStatsAccessRecordReqDTO shortLinkStatsAccessRecordReqDTO){
        return Results.success(shortLinkStatisticService.shortLinkStatsAccessRecord(shortLinkStatsAccessRecordReqDTO));
    }

    /**
     *
     * 短链接组指定时间内访问记录监控数据【log数据】
     */
    @PostMapping("/access-record/group")
    public Result<IPage<ShortLinkStatsAccessRecordGroupRespDTO>> shortLinkAccessRecordGroupStats(@RequestBody ShortLinkStatsAccessRecordGroupReqDTO shortLinkStatsAccessRecordGroupReqDTO){
        return Results.success(shortLinkStatisticService.shortLinkStatsAccessRecordGroup(shortLinkStatsAccessRecordGroupReqDTO));
    }

}
