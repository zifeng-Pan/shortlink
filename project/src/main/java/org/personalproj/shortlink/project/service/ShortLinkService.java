package org.personalproj.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCountQueryRespDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.io.IOException;
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

    /**
     * @description: 根据前端发送的shortLinkUpdateReqDTO以及gid进行短链接地更新
     * @author: PzF
     * @date: 2024/3/9 21:42
     * @param: [shortLinkUpdateReqDTO]
     * @return: void
     **/
    void shortLinkUpdate(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO);


    /**
     * @description: 根据oldGid,id进行短链接组修改
     * @author: PzF
     * @date: 2024/3/9 23:03
     * @param: [oldGid,id, gid]
     * @return: void
     **/
    void shortLinkChangeGroup(String oldGid, Long id, String gid);

    /**
     * @description:
     * @author: PzF
     * @date: 2024/3/10 0:06
     * @param: [gid, id]
     * @return: void
     **/
    void shortLinkDelete(String gid, Long id);

    /**
     * @description: 短链接跳转
     * @author: PzF
     * @date: 2024/3/10 19:47
     * @param: [shortUri：短链接后缀, httpServletRequest：Http请求, httpServletResponse：Http响应]
     * @return: void
     **/
    void restoreUrl(String shortUri, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InterruptedException, IOException;
}
