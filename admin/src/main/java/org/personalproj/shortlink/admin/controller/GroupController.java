package org.personalproj.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.common.convention.result.Result;
import org.personalproj.shortlink.admin.common.convention.result.Results;
import org.personalproj.shortlink.admin.dto.req.GroupUpdateDTO;
import org.personalproj.shortlink.admin.dto.resp.GroupRespDTO;
import org.personalproj.shortlink.admin.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.controller
 * @Author: PzF
 * @CreateTime: 2024-01-31  13:33
 * @Description: 针对短链接分组的Controller
 * @Version: 1.0
 */

@RestController
@RequestMapping("/api/shortlink/v1/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     *
     * 新生成一个短链接组
     */
    @GetMapping("/save/{groupname}")
    public Result<Void> saveNewGroupByGroupName(@PathVariable("groupname") String groupName){
        groupService.saveGroup(groupName);
        return Results.success();
    }

    /**
     *
     * 获取当前登录用户的所有短链接组
     */
    @GetMapping("/list")
    public Result<List<GroupRespDTO>> getGroups(){
        return Results.success(groupService.getGroups());
    }

    /**
     *
     * 更新短链接组
     */
    @PutMapping("/update")
    public Result<Void> updateGroup(@RequestBody GroupUpdateDTO groupUpdateDTO){
        groupService.updateGroup(groupUpdateDTO);
        return Results.success();
    }

    @GetMapping("/remove/{gid}")
    public Result<Void> removeGroup(@PathVariable("gid") String gid){
        groupService.removeGroup(gid);
        return Results.success();
    }


}