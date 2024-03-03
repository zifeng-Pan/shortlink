package org.personalproj.shortlink.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.common.convention.result.Result;
import org.personalproj.shortlink.admin.common.convention.result.Results;
import org.personalproj.shortlink.admin.dto.req.UserLoginReqDTO;
import org.personalproj.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.personalproj.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.personalproj.shortlink.admin.dto.resp.UserActualRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserRespDTO;
import org.personalproj.shortlink.admin.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.controller
 * @Author: PzF
 * @CreateTime: 2024-01-27  10:48
 * @Description: 用户管理控制层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/shortlink/admin/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * @description: 根据用户名查询信息
     **/
    @GetMapping("/common/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        UserRespDTO result = userService.getUserByUserName(username);
        return Results.success(result);

    }


    /**
     * 获取用户真实未脱敏信息
     */
    @GetMapping("/common/actual/{username}")
    public Result<UserActualRespDTO> getUserActualInfoByUsername(@PathVariable("username") String username) {
        UserActualRespDTO result = userService.getUserActualInfoByUserName(username);
        return Results.success(result);
    }

    @GetMapping("/common/nickname-available/{nickname}")
    public Result<Boolean> userNickNameAvailable(@PathVariable("nickname") String nickname){
        return Results.success(!userService.hasNickName(nickname));
    }


    /**
     *
     * 用户登录
     */
    @PostMapping("/common/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO userLoginReqDTO){
        UserLoginRespDTO respDTO = userService.login(userLoginReqDTO);
        return Results.success(respDTO);
    }

    /**
     *
     * 用户登出
     */
    @GetMapping("/logout")
    public Result<Void> logOut(HttpServletRequest request){
        userService.logOut(request);
        return Results.success();
    }


    /**
     * 新用户注册
     */
    @PostMapping("/common/register")
    public Result<String> userRegister(@RequestBody UserRegisterReqDTO userRegisterReqDTO) {
        return Results.success(userService.register(userRegisterReqDTO));
    }


    /**
     * 用户信息更新
     */
    @PutMapping("/update")
    public Result<Void> userUpdate(@RequestBody UserUpdateReqDTO userUpdateReqDTO) {
        userService.update(userUpdateReqDTO);
        return Results.success();
    }

}
