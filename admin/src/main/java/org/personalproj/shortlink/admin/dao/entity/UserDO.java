package org.personalproj.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.personalproj.shortlink.common.database.BaseDO;

import java.io.Serial;
import java.io.Serializable;

/**
 * 
 * @TableName t_user
 */
@TableName(value ="t_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDO extends BaseDO implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     *
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 用户邮箱
     */
    private String mail;

    /**
     * 用户注销时间戳
     */
    private Long deletionTime;

    @TableField(exist = false)
    @Serial
    private static final long serialVersionUID = 1L;
}