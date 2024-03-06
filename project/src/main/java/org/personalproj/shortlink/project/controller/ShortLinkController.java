package org.personalproj.shortlink.project.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.common.convention.result.Result;
import org.personalproj.shortlink.common.convention.result.Results;
import org.personalproj.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.project.service.ShortLinkService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.controller
 * @Author: PzF
 * @CreateTime: 2024-03-03  14:05
 * @Description: 短链接控制层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/shortlink/project/v1/core")
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @PostMapping("/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO shortLinkCreateReqDTO){
        return Results.success(shortLinkService.create(shortLinkCreateReqDTO));
    }
}
