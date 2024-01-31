package org.personalproj.shortlink.admin.toolkit;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import org.personalproj.shortlink.admin.dto.req.UserLoginReqDTO;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.toolkit
 * @Author: PzF
 * @CreateTime: 2024-01-30  21:14
 * @Description: JWT工具类
 * @Version: 1.0
 */
public class JWTUtil {

    private final static String USER_LOGIN_SECRET_KEY = "org.personalproj.short-link.user.login.secret.key";

    public static String generateToken(UserLoginReqDTO userLoginReqDTO){
        DateTime now = DateTime.now();
        DateTime newTime = now.offsetNew(DateField.MINUTE, 30);
        String secretKey = USER_LOGIN_SECRET_KEY + now;

        HashMap<String, Object> payload = new HashMap<>();
        //签发时间
        payload.put(JWTPayload.ISSUED_AT, now);
        //过期时间
        payload.put(JWTPayload.EXPIRES_AT, newTime);
        //生效时间
        payload.put(JWTPayload.NOT_BEFORE, now);
        // 用户名
        payload.put("username",userLoginReqDTO.getUsername());
        // 密码
        payload.put("password",userLoginReqDTO.getPassword());

        return cn.hutool.jwt.JWTUtil.createToken(payload, secretKey.getBytes());
    }

    public static boolean tokenVerify(String token) {
        JWT jwt = cn.hutool.jwt.JWTUtil.parseToken(token);
        return jwt.setKey(USER_LOGIN_SECRET_KEY.getBytes(StandardCharsets.UTF_8)).validate(1);
    }
}
