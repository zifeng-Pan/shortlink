package org.personalproj.shortlink.admin.remote.dto.resp.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.resp
 * @Author: PzF
 * @CreateTime: 2024-03-14  18:53
 * @Description: 短链接组监控访问记录响应实体
 * @Version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsAccessRecordGroupRespDTO {

    /**
     *
     * 用户cookie
     */
    private String user;

    /**
     *
     * 用户类型
     */
    private String userType;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * ip
     */
    private String ip;

    /**
     * 日期
     */
    private Date date;

    /**
     *
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 短链接网络
     */
    private String network;

    /**
     *
     * 短链接设备
     */
    private String device;

    /**
     *
     * 短链接位置
     */
    private String locale;

}
