package org.personalproj.shortlink.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.remote.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-09  20:39
 * @Description: 短链接更新DTO
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkUpdateReqDTO {

    /**
     *
     * 分组标识
     */
    private String gid;

    /**
     *
     * 短链接标记
     */
    private Long id;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 有效期类型 0：永久有效， 1：自定义有效期时间
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    /**
     * 短链接相关描述
     */
    private String description;

    /**
     *
     * 短链接相关图标
     */
    private String favicon;

}
