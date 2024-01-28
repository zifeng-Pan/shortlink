package org.personalproj.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.common.convention.exception.ClientException;
import org.personalproj.shortlink.admin.common.convention.result.Result;
import org.personalproj.shortlink.admin.common.convention.result.Results;
import org.personalproj.shortlink.admin.common.enums.UserErrorCode;
import org.personalproj.shortlink.admin.dto.resp.UserActualRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserRespDTO;
import org.personalproj.shortlink.admin.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *@BelongsProject: shortlink
 *@BelongsPackage: org.personalproj.shortlink.admin.controller
 *@Author: PzF
 *@CreateTime: 2024-01-27  10:48
 *@Description: 用户管理控制层
 *@Version: 1.0
 */
@RestController
@RequestMapping("/api/shortlink/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * @description: 根据用户名查询信息
     **/
    @GetMapping("{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
        UserRespDTO result = userService.getUserByUserName(username);
        if (result == null){
            throw new ClientException(UserErrorCode.USER_NULL);
        }else {
            return Results.success(result);
        }
    }

    @GetMapping("/actual/{username}")
    public Result<UserActualRespDTO> getUserActualInfoByUsername(@PathVariable("username") String username){
        UserActualRespDTO result = userService.getUserActualInfoByUserName(username);
        if (result == null){
            throw new ClientException(UserErrorCode.USER_NULL);
        }else {
            return Results.success(result);
        }
    }

}
