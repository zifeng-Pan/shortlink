package org.personalproj.shortlink.admin.remote;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.personalproj.shortlink.common.convention.result.Result;

/**
 *
 * 这个接口主要是用来远程调用短链接中心的核心功能用的，后期会将这部分功能改造成SpringCloud中的OpenFeign进行远程调用
 * Version1：采用Http请求的方式从后管系统进行请求的发出
 */
public interface ShortLinkRemoteService {

    /**
     * 通过HttpUtil发送Post的短链接分页请求
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO){

        HttpRequest httpRequest = HttpUtil.createPost("http://127.0.0.1:8001/api/shortlink/project/v1/core/page").body(JSON.toJSONString(shortLinkPageReqDTO));
        HttpResponse execute = httpRequest.execute();
        // 主要是利用HuTool的工具向短链接后台核心的Controller发送远程调用请求，根据返回来的Json数据解析成对象并返回
        return JSON.parseObject(execute.body(), Result.class);
    }


    /**
     *
     * 通过HttpUtil发送Post的短链接创建请求
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO){
        HttpRequest httpRequest = HttpUtil.createPost("http://127.0.0.1:8001/api/shortlink/project/v1/core/create").body(JSON.toJSONString(shortLinkCreateReqDTO));
        return JSON.parseObject(httpRequest.execute().body(),Result.class);
    }

}
