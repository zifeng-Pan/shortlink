package org.personalproj.shortlink.project.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.common.convention.result.Result;
import org.personalproj.shortlink.common.convention.result.Results;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.personalproj.shortlink.project.dto.resp.stats.ShortLinkStatsRespDTO;
import org.personalproj.shortlink.project.service.ShortLinkStatisticService;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/short-link/v1")
public class ShortLinkStatisticController {

    private final ShortLinkStatisticService shortLinkStatisticService;

    @GetMapping("/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(@RequestBody ShortLinkStatsReqDTO shortLinkStatsReqDTO){
        return Results.success(shortLinkStatisticService.singeShortLinkStatistic(shortLinkStatsReqDTO));
    }

    @GetMapping("/stats/group")
    public Result<ShortLinkStatsRespDTO> shortLinkGroupStats(@RequestBody ShortLinkGroupStatsReqDTO ShortLinkGroupStatsReqDTO){
        return Results.success(shortLinkStatisticService.groupShortLinkStats(ShortLinkGroupStatsReqDTO));
    }

}
