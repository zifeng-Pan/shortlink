package org.personalproj.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.common.convention.result.Result;
import org.personalproj.shortlink.admin.common.convention.result.Results;
import org.personalproj.shortlink.admin.dto.resp.GroupRespDTO;
import org.personalproj.shortlink.admin.service.GroupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{groupname}")
    public Result<Void> saveNewGroupByGroupName(@PathVariable("groupname") String groupName){
        groupService.saveGroup(groupName);
        return Results.success();
    }

    @GetMapping("/list")
    public Result<List<GroupRespDTO>> getGroups(){
        return Results.success(groupService.getGroups());
    }
}
