package org.personalproj.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

/**
* @author panzifeng
* @description 针对表【t_link】的数据库操作Service
* @createDate 2024-03-03 13:33:45
*/
public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * @description: 根据用户POST生成的短链接创建DTO，生成短链接并保存进入数据库，同时返回响应DTO
     * @author: PzF
     * @date: 2024/3/6 14:00
     * @param: [shortLinkCreateReqDTO]
     * @return: org.personalproj.shortlink.project.dto.resp.ShortLinkCreateRespDTO
     **/
    ShortLinkCreateRespDTO create(ShortLinkCreateReqDTO shortLinkCreateReqDTO);

    /**
     * @description: 根据分组标识查询到某一分组下面的短链接，并分页返回
     * @author: PzF
     * @date: 2024/3/6 17:26
     * @param: [shortLinkPageReqDTO]
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<org.personalproj.shortlink.project.dto.resp.ShortLinkPageRespDTO>
     **/
    IPage<ShortLinkPageRespDTO> pageQuery(ShortLinkPageReqDTO shortLinkPageReqDTO);

    /**
     * @description: 根据gidList进行用户下不同的短链接分组下面的短链接数量查询
     * @author: PzF
     * @date: 2024/3/7 10:01
     * @param: [gidList: 当前登录用户的所有短链接组的gid列表]
     * @return: org.personalproj.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO
     **/
    List<ShortLinkCountQueryRespDTO> shortLinkCountQuery(List<String> gidList);
}
