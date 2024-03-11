package org.personalproj.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleBinRecoverReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkRecycleReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkRecycleBinPageRespDTO;

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


    /**
     * @description: 根据分组标识查询到某一分组下面的短链接，并分页返回
     * @author: PzF
     * @date: 2024/3/11 14:39
     * @param: [shortLinkRecycleBinPageReqDTO]
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<org.personalproj.shortlink.project.dto.resp.ShortLinkRecycleBinPageRespDTO>
     **/
    IPage<ShortLinkRecycleBinPageRespDTO> pageQuery(ShortLinkRecycleBinPageReqDTO shortLinkRecycleBinPageReqDTO);

    /**
     * @description: 根据恢复请求进行回收站内短链接的恢复
     * @author: PzF
     * @date: 2024/3/11 15:58
     * @param: [shortLinkRecycleBinRecoverReqDTO]
     * @return: void
     **/
    void recover(ShortLinkRecycleBinRecoverReqDTO shortLinkRecycleBinRecoverReqDTO);
}
