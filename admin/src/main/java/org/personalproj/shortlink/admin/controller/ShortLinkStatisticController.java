package org.personalproj.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.remote.ShortLinkRemoteService;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.stats.ShortLinkStatsRespDTO;
import org.personalproj.shortlink.common.convention.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.controller
 * @Author: PzF
 * @CreateTime: 2024-03-18  09:51
 * @Description: 短链接监控远程调用控制层
 * @Version: 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/v1/stats")
public class ShortLinkStatisticController {

    private final ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};

    @PostMapping("/single")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(@RequestBody ShortLinkStatsReqDTO shortLinkStatsReqDTO){
        return shortLinkRemoteService.singleShortLinkStatistic(shortLinkStatsReqDTO);
    }

    @PostMapping("/group")
    public Result<ShortLinkStatsRespDTO> shortLinkGroupStats(@RequestBody ShortLinkGroupStatsReqDTO ShortLinkGroupStatsReqDTO){
        return shortLinkRemoteService.groupShortLinkStats(ShortLinkGroupStatsReqDTO);
    }
}
