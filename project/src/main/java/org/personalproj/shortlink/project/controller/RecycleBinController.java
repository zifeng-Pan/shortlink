package org.personalproj.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.common.convention.result.Result;
import org.personalproj.shortlink.common.convention.result.Results;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleBinRecoverReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleBinRemoveReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkRecycleBinPageRespDTO;
import org.personalproj.shortlink.project.service.RecycleBinService;
import org.springframework.web.bind.annotation.*;

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

    /**
     *
     * 短链接加入回收站
     */
    @PostMapping("/recycle")
    public Result<Void> recycle(@RequestBody ShortLinkRecycleReqDTO shortLinkRecycleReqDTO){
        recycleBinService.shortLinkRecycle(shortLinkRecycleReqDTO);
        return Results.success();
    }


    /**
     *
     * 短链接回收站分页查询
     */
    @PostMapping("/page")
    public Result<IPage<ShortLinkRecycleBinPageRespDTO>> pageQuery(@RequestBody ShortLinkRecycleBinPageReqDTO shortLinkRecycleBinPageReqDTO){
        return Results.success(recycleBinService.pageQuery(shortLinkRecycleBinPageReqDTO));
    }

    @PutMapping("/recover")
    public Result<Void> recover(@RequestBody ShortLinkRecycleBinRecoverReqDTO shortLinkRecycleBinRecoverReqDTO){
        recycleBinService.recover(shortLinkRecycleBinRecoverReqDTO);
        return Results.success();
    }

    @DeleteMapping("/remove")
    public Result<Void> remove(@RequestBody ShortLinkRecycleBinRemoveReqDTO shortLinkRecycleBinRemoveReqDTO){
        recycleBinService.shortLinkRemove(shortLinkRecycleBinRemoveReqDTO);
        return Results.success();
    }

}
