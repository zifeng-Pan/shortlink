package org.personalproj.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.personalproj.shortlink.admin.remote.ShortLinkRemoteService;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.personalproj.shortlink.common.convention.result.Result;
import org.personalproj.shortlink.common.convention.result.Results;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Result<Map<String,Integer>> page(@RequestParam(value = "gidList") List<String> gidList){
        Result<List<ShortLinkCountQueryRespDTO>> listResult = shortLinkRemoteService.countShortLinkByGroup(gidList);
        List<ShortLinkCountQueryRespDTO> shortLinkCountQueryRespDTOList = listResult.getData();
        Map<String, Integer> shortLinkCountQueryRespMap = new HashMap<>(5);
        shortLinkCountQueryRespDTOList
                .forEach(
                shortLinkCountQueryRespDTO -> {
                    shortLinkCountQueryRespMap.put(shortLinkCountQueryRespDTO.getGid(),shortLinkCountQueryRespDTO.getShortLinkCount());
                });
        return Results.success(shortLinkCountQueryRespMap);
    }

    @PutMapping("/update")
    public Result<Void> shortLinkUpdate(@RequestBody ShortLinkUpdateReqDTO shortLinkUpdateReqDTO){
        return shortLinkRemoteService.shortLinkUpdate(shortLinkUpdateReqDTO);
    }

    @PutMapping("/change/group")
    public Result<Void> shortLinkChangeGroup(@RequestParam("oldGid") String oldGid, @RequestParam("id") Long id, @RequestParam("gid") String gid){
        return shortLinkRemoteService.shortLinkChangeGroup(oldGid, id, gid);
    }

    @DeleteMapping("/del")
    public Result<Void> shortLinkDelete(@RequestParam("gid") String gid, @RequestParam("id") Long id){
        return shortLinkRemoteService.shortLinkDelete(gid,id);
    }
}
