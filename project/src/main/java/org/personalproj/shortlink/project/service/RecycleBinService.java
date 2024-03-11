package org.personalproj.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleReqDTO;

/**
 *
 * 短链接回收站服务接口
 */
public interface RecycleBinService extends IService<ShortLinkDO> {

    /**
     * @description: 短链接回收
     * @author: PzF
     * @date: 2024/3/11 12:32
     * @param: [shortLinkRecycleReqDTO：短链接回收请求DTO]
     * @return: void
     **/
    void shortLinkRecycle(ShortLinkRecycleReqDTO shortLinkRecycleReqDTO);
}
