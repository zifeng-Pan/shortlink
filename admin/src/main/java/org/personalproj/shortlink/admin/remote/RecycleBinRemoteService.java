package org.personalproj.shortlink.admin.remote;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import org.personalproj.shortlink.admin.remote.dto.req.ShortLinkRecycleReqDTO;
import org.personalproj.shortlink.common.convention.result.Result;

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
}
