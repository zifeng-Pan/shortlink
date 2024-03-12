package org.personalproj.shortlink.admin.remote;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkRecycleBinRecoverReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkRecycleBinRemoveReqDTO;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkRecycleReqDTO;
import org.personalproj.shortlink.admin.remote.dto.resp.ShortLinkRecycleBinPageRespDTO;
import org.personalproj.shortlink.common.convention.result.Result;

import java.lang.reflect.Type;

public interface RecycleBinRemoteService {

    /**
     * @description: 远端调用回收站回收功能
     * @author: PzF
     * @date: 2024/3/11 13:10
     * @param: [shortLinkRecycleReqDTO]
     * @return: org.personalproj.shortlink.common.convention.result.Result<java.lang.Void>
     **/
    default Result<Void> recycle(ShortLinkRecycleReqDTO shortLinkRecycleReqDTO){
        HttpRequest httpRequest = HttpUtil.createPost("http://127.0.0.1:8001/api/shortlink/project/v1/core/recycle-bin/recycle").body(JSON.toJSONString(shortLinkRecycleReqDTO));
        return JSON.parseObject(httpRequest.execute().body(), Result.class);
    };

    default Result<IPage<ShortLinkRecycleBinPageRespDTO>> pageQuery(ShortLinkRecycleBinPageReqDTO shortLinkRecycleBinPageReqDTO){
        HttpRequest request = HttpUtil.createPost("http://127.0.0.1:8001/api/shortlink/project/v1/core/recycle-bin/page").body(JSON.toJSONString(shortLinkRecycleBinPageReqDTO));
        Type type = new TypeReference<Result<IPage<ShortLinkRecycleBinPageRespDTO>>>() {
        }.getType();
        return JSON.parseObject(request.execute().body(), type);
    };

    default Result<Void> recover(ShortLinkRecycleBinRecoverReqDTO shortLinkRecycleBinRecoverReqDTO){
        String requestUrl = "http://127.0.0.1:8001/api/shortlink/project/v1/core/recycle-bin/recover";
        HttpRequest putReq = HttpUtil.createRequest(Method.PUT, requestUrl);
        putReq.body(JSON.toJSONString(shortLinkRecycleBinRecoverReqDTO));
        return JSON.parseObject(putReq.execute().body(),Result.class);
    };

    default Result<Void> remove(ShortLinkRecycleBinRemoveReqDTO shortLinkRecycleBinRemoveReqDTO){
        String requestUrl = "http://127.0.0.1:8001/api/shortlink/project/v1/core/recycle-bin/remove";
        HttpRequest putReq = HttpUtil.createRequest(Method.DELETE, requestUrl);
        putReq.body(JSON.toJSONString(shortLinkRecycleBinRemoveReqDTO));
        return JSON.parseObject(putReq.execute().body(), Result.class);
    };
}
