package org.personalproj.shortlink.admin.service;

import org.personalproj.shortlink.admin.dao.entity.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.personalproj.shortlink.admin.dto.resp.UserActualRespDTO;
import org.personalproj.shortlink.admin.dto.resp.UserRespDTO;

/**
* @author panzifeng
* @description 针对表【t_user】的数据库操作Service
* @createDate 2024-01-27 12:55:52
*/
public interface UserService extends IService<UserDO> {

    UserRespDTO getUserByUserName(String username);

    UserActualRespDTO getUserActualInfoByUserName(String username);

    /**
     *
     * 查看是否存在相应的username
     */
    Boolean hasUserName(String username);
}
