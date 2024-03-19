package org.personalproj.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-06  17:14
 * @Description: 短链接分页请求参数[该类继承Page类，可以设置当前页，页数等参数，查询的时候返回的页对象按照这个类的设定值返回]
 * @Version: 1.0
 */
@Data
public class ShortLinkPageReqDTO extends Page {
    /**
     * 所属短链接组的gid
     */
    private String gid;

    /**
     *
     * 排序标识
     */
    private String orderTag;
}
