package org.personalproj.shortlink.admin.toolkit;

import org.personalproj.shortlink.admin.dto.resp.UserLoginRespDTO;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.toolkit
 * @Author: PzF
 * @CreateTime: 2024-01-30  20:14
 * @Description: 利用ThreadLocal保存用户
 * @Version: 1.0
 */
public class UserHolder {
    private static final ThreadLocal<UserLoginRespDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserLoginRespDTO user){
        tl.set(user);
    }

    public static UserLoginRespDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
