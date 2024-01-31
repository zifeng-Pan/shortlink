package org.personalproj.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.service.GroupService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
