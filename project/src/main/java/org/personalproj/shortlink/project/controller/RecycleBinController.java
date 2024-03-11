package org.personalproj.shortlink.project.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.common.convention.result.Result;
import org.personalproj.shortlink.common.convention.result.Results;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleReqDTO;
import org.personalproj.shortlink.project.service.RecycleBinService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.controller
 * @Author: PzF
 * @CreateTime: 2024-03-11  12:18
 * @Description: 回收站管理Controller
 * @Version: 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shortlink/project/v1/core/recycle-bin")
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    @PostMapping("/recycle")
    public Result<Void> recycle(@RequestBody ShortLinkRecycleReqDTO shortLinkRecycleReqDTO){
        recycleBinService.shortLinkRecycle(shortLinkRecycleReqDTO);
        return Results.success();
    }


}
