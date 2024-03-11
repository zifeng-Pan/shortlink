package org.personalproj.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.remote.RecycleBinRemoteService;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkRecycleReqDTO;
import org.personalproj.shortlink.common.convention.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.controller
 * @Author: PzF
 * @CreateTime: 2024-03-11  13:02
 * @Description: 后管系统远程调用回收站Controller
 * @Version: 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shortlink/admin/v1/core/recycle-bin")
public class RecycleBinController {
    private final RecycleBinRemoteService recycleBinRemoteService = new RecycleBinRemoteService() {
    };

    /**
     *
     * 短链接回收站
     */
    @PostMapping("/recycle")
    public Result<Void> recycle(@RequestBody ShortLinkRecycleReqDTO shortLinkRecycleReqDTO){
        return recycleBinRemoteService.recycle(shortLinkRecycleReqDTO);
    }
}
