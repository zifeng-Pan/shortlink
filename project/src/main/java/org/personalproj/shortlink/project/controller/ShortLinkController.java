package org.personalproj.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.common.convention.result.Result;
import org.personalproj.shortlink.common.convention.result.Results;
import org.personalproj.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.personalproj.shortlink.project.service.ShortLinkService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/page")
    public Result<IPage<ShortLinkPageRespDTO>> shortLinkPageQuery(@RequestBody ShortLinkPageReqDTO shortLinkPageReqDTO){
        return Results.success(shortLinkService.pageQuery(shortLinkPageReqDTO));
    }
}
