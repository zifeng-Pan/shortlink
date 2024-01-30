package org.personalproj.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.common.convention.result.Result;
import org.personalproj.shortlink.admin.common.convention.result.Results;
import org.personalproj.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.personalproj.shortlink.admin.dto.resp.UserActualRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserRespDTO;
import org.personalproj.shortlink.admin.service.UserService;
import org.springframework.web.bind.annotation.*;

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
        return Results.success(result);

    }


    /**
     *
     * 获取用户真实未脱敏信息
     */
    @GetMapping("/actual/{username}")
    public Result<UserActualRespDTO> getUserActualInfoByUsername(@PathVariable("username") String username){
        UserActualRespDTO result = userService.getUserActualInfoByUserName(username);
        return Results.success(result);
    }


    /**
     *
     * 判断用户名是否可用
     */
    @GetMapping("/has-username/{username}")
    public Result<Boolean> hasUserName(@PathVariable("username") String username){
        return Results.success(userService.hasUserName(username));
    }



    @PostMapping("/register")
    public Result<Void> userRegister(@RequestBody UserRegisterReqDTO userRegisterReqDTO){
        userService.register(userRegisterReqDTO);
        return Results.success();
    }

}
