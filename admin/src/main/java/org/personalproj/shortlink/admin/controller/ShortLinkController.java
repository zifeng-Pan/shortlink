package org.personalproj.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.personalproj.shortlink.admin.remote.ShortLinkRemoteService;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.personalproj.shortlink.common.convention.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.controller
 * @Author: PzF
 * @CreateTime: 2024-03-06  20:56
 * @Description: 短链接后管中心远程调用短链接核心的Controller
 * @Version: 1.0
 */

@RestController
@AllArgsConstructor
@RequestMapping("/api/shortlink/admin/v1/core")
public class ShortLinkController {

    private final ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @PostMapping("/create")
    public Result<ShortLinkCreateRespDTO> create(@RequestBody ShortLinkCreateReqDTO shortLinkCreateReqDTO){
        return shortLinkRemoteService.createShortLink(shortLinkCreateReqDTO);
    }

    @PostMapping("/page")
    public Result<IPage<ShortLinkPageRespDTO>> page(@RequestBody ShortLinkPageReqDTO shortLinkPageReqDTO){
        return shortLinkRemoteService.pageShortLink(shortLinkPageReqDTO);
    }

    @GetMapping("/count")
    public Result<List<ShortLinkCountQueryRespDTO>> page(@RequestParam(value = "gidList") List<String> gidList){
        return shortLinkRemoteService.countShortLinkByGroup(gidList);
    }




}
