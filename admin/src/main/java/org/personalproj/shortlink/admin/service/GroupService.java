package org.personalproj.shortlink.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.personalproj.shortlink.admin.dao.entity.GroupDO;
import org.personalproj.shortlink.admin.dto.req.GroupSortReqDTO;
import org.personalproj.shortlink.admin.dto.req.GroupUpdateReqDTO;
import org.personalproj.shortlink.admin.dto.resp.GroupRespDTO;

import java.util.List;

/**
 * @author panzifeng
 * @description 针对表【t_group】的数据库操作Service
 * @createDate 2024-01-31 13:30:52
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * @description: 新增短链接分组
     * @author: PzF
     * @date: 2024/1/31 14:39
     * @param: [groupName：短链接分组名]
     * @return: void
     **/
    void saveGroup(String groupName);

    /**
     * @description: 根据分组名称和用户名称进行分组新增
     * @author: PzF
     * @date: 2024/3/9 18:40
     * @param: [groupName, username]
     * @return: void
     **/
    void saveGroup(String groupName, String username);

    List<GroupRespDTO> getGroups();

    void updateGroup(GroupUpdateReqDTO groupUpdateReqDTO);

    void removeGroup(String gid);

    void sortOrderUpdate(List<GroupSortReqDTO> groupSortReqDTOS);
}
