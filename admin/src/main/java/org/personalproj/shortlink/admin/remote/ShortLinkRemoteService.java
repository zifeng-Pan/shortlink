package org.personalproj.shortlink.admin.remote;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.personalproj.shortlink.admin.remote.dto.req.*;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkCountQueryRespDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.stats.ShortLinkStatsAccessRecordRespDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.stats.ShortLinkStatsRespDTO;
import org.personalproj.shortlink.common.convention.result.Result;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 主要是利用HuTool的工具向短链接后台核心的Controller发送远程调用请求，根据返回来的Json数据解析成对象并返回
        return JSON.parseObject(httpRequest.execute().body(), Result.class);
    }


    /**
     *
     * 通过HttpUtil发送Post的短链接创建请求
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO){
        HttpRequest httpRequest = HttpUtil.createPost("http://127.0.0.1:8001/api/shortlink/project/v1/core/create").body(JSON.toJSONString(shortLinkCreateReqDTO));
        return JSON.parseObject(httpRequest.execute().body(),Result.class);
    }


    /**
     *
     * 通过HttpUtil发送Get的短链接组内短链接计数请求
     */
    default Result<List<ShortLinkCountQueryRespDTO>> countShortLinkByGroup(List<String> gidList){
        Map<String, Object> requestMap = new HashMap<>(5);
        requestMap.put("gidList", gidList);
        String returnUrl = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/project/v1/core/count", requestMap);
        Type type = new TypeReference<Result<List<ShortLinkCountQueryRespDTO>>>() {
        }.getType();
        return JSON.parseObject(returnUrl, type);
    }

    default Result<Void> shortLinkUpdate(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO){
        String requestUrl = "http://127.0.0.1:8001/api/shortlink/project/v1/core/update";
        HttpRequest putReq = HttpUtil.createRequest(Method.PUT, requestUrl);
        putReq.body(JSON.toJSONString(shortLinkUpdateReqDTO));
        return JSON.parseObject(putReq.execute().body(),Result.class);
    }

    default Result<Void> shortLinkChangeGroup(String oldGid, Long id, String gid){
        String requestUrl = "http://127.0.0.1:8001/api/shortlink/project/v1/core/change/group";
        HttpRequest putReq = HttpUtil.createRequest(Method.PUT, requestUrl);
        putReq.form("oldGid",oldGid);
        putReq.form("id",id);
        putReq.form("gid",gid);
        return JSON.parseObject(putReq.execute().body(),Result.class);
    }

    default Result<Void> shortLinkDelete(String gid, Long id){
        String requestUrl = "http://127.0.0.1:8001/api/shortlink/project/v1/core/del";
        HttpRequest putReq = HttpUtil.createRequest(Method.DELETE, requestUrl);
        putReq.form("gid",gid);
        putReq.form("id",id);
        return JSON.parseObject(putReq.execute().body(),Result.class);
    }

    default Result<String> getTitleByUrl(String url){
        String responseContent = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/title?url=" + url);
        Type type = new TypeReference<Result<String>>() {
        }.getType();
        return JSON.parseObject(responseContent, type);
    }

    /**
     *
     * 远程调用后台单个短链接监控数据统计
     */
    default Result<ShortLinkStatsRespDTO> singleShortLinkStatistic(ShortLinkStatsReqDTO shortLinkStatsReqDTO){
        HttpRequest httpRequest = HttpUtil.createPost("http://127.0.0.1:8001/api/short-link/v1/stats/single").body(JSON.toJSONString(shortLinkStatsReqDTO));
        return JSON.parseObject(httpRequest.execute().body(), Result.class);
    }

    /**
     * 远程调用后台短链接组监控数据统计
     */
    default Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO){
        HttpRequest httpRequest = HttpUtil.createPost("http://127.0.0.1:8001/api/short-link/v1/stats/group").body(JSON.toJSONString(shortLinkGroupStatsReqDTO));
        return JSON.parseObject(httpRequest.execute().body(),Result.class);
    }

    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO shortLinkStatsAccessRecordReqDTO){
        HttpRequest httpRequest = HttpUtil.createPost("http://127.0.0.1:8001/api/short-link/v1/stats/access-record").body(JSON.toJSONString(shortLinkStatsAccessRecordReqDTO));
        return JSON.parseObject(httpRequest.execute().body(),Result.class);
    };
}
