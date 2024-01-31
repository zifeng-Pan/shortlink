package org.personalproj.shortlink.admin.service;

import jakarta.servlet.http.HttpServletRequest;
import org.personalproj.shortlink.admin.dao.entity.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.personalproj.shortlink.admin.dto.req.UserLoginReqDTO;
import org.personalproj.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.personalproj.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.personalproj.shortlink.admin.dto.resp.UserActualRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserRespDTO;

/**
 * @author panzifeng
 * @description 针对表【t_user】的数据库操作Service
 * @createDate 2024-01-27 12:55:52
 */
public interface UserService extends IService<UserDO> {

    UserRespDTO getUserByUserName(String username);

    UserActualRespDTO getUserActualInfoByUserName(String username);


    /**
     * @description: 用户登录
     * @author: PzF
     * @date: 2024/1/30 14:58
     * @param: [userLoginReqDTO: 用户登录请求DTO]
     * @return: org.personalproj.shortlink.admin.dto.resp.UserLoginRespDTO：用户登录返回DTO
     **/
    UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO);

    /**
     * 用户注册
     *
     * @return
     */
    String register(UserRegisterReqDTO userRegisterReqDTO);

    /**
     * 用户更新
     */

    void update(UserUpdateReqDTO userUpdateReqDTO);

    /**
     * 检查用户是否已经登入
     */
    Boolean isLogin(String token);

    /**
     * 用户登出
     */
    void logOut(HttpServletRequest request);

    Boolean hasNickName(String nickName);

}
