package org.personalproj.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
* @author panzifeng
* @description 针对表【t_link】的数据库操作Service
* @createDate 2024-03-03 13:33:45
*/
public interface ShortLinkService extends IService<ShortLinkDO> {

    ShortLinkCreateRespDTO create(ShortLinkCreateReqDTO shortLinkCreateReqDTO);
}
