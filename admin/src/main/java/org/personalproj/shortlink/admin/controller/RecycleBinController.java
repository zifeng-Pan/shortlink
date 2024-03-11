package org.personalproj.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.dto.resp.GroupRespDTO;
import org.personalproj.shortlink.admin.remote.RecycleBinRemoteService;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkRecycleBinRecoverReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkRecycleReqDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkRecycleBinPageRespDTO;
import org.personalproj.shortlink.admin.service.GroupService;
import org.personalproj.shortlink.common.convention.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    private final GroupService groupService;

    /**
     *
     * 短链接回收站
     */
    @PostMapping("/recycle")
    public Result<Void> recycle(@RequestBody ShortLinkRecycleReqDTO shortLinkRecycleReqDTO){
        return recycleBinRemoteService.recycle(shortLinkRecycleReqDTO);
    }

    /**
     *
     * 短链接回收站分页查询
     */
    @GetMapping("/page")
    public Result<IPage<ShortLinkRecycleBinPageRespDTO>> pageQuery(@RequestBody ShortLinkRecycleBinPageReqDTO shortLinkRecycleBinPageReqDTO){
        List<GroupRespDTO> groups = groupService.getGroups();
        List<String> gidList = new ArrayList<>();
        groups.forEach(groupRespDTO -> gidList.add(groupRespDTO.getGid()));
        shortLinkRecycleBinPageReqDTO.setGidList(gidList);
        return recycleBinRemoteService.pageQuery(shortLinkRecycleBinPageReqDTO);
    }

    @PutMapping("/recover")
    public Result<Void> recover(@RequestBody ShortLinkRecycleBinRecoverReqDTO shortLinkRecycleBinRecoverReqDTO){
        return recycleBinRemoteService.recover(shortLinkRecycleBinRecoverReqDTO);
    }
}
