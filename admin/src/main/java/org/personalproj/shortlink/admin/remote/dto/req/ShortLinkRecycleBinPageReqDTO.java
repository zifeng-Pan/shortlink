package org.personalproj.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.remote.dto.req
 * @Author: PzF
 * @CreateTime: 2024-03-11  14:38
 * @Description: 短链接回收站分页查询DTO
 * @Version: 1.0
 */

@Data
public class ShortLinkRecycleBinPageReqDTO extends Page {

    /**
     *
     * 当前用户的分组id构成的列表
     */
    List<String> gidList;
}
