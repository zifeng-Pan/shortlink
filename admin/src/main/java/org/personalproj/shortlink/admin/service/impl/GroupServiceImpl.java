package org.personalproj.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.common.convention.exception.ClientException;
import org.personalproj.shortlink.admin.dao.entity.GroupDO;
import org.personalproj.shortlink.admin.dao.mapper.GroupMapper;
import org.personalproj.shortlink.admin.dto.resp.GroupRespDTO;
import org.personalproj.shortlink.admin.service.GroupService;
import org.personalproj.shortlink.admin.toolkit.UserHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.personalproj.shortlink.admin.common.enums.GroupErrorCode.GROUP_SAVE_ERROR;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.service.impl
 * @Author: PzF
 * @CreateTime: 2024-01-31  13:30
 * @Description: 针对[t_group]的Service实现类
 * @Version: 1.0
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid;
        String username = UserHolder.getUser().getUsername();
        do{
            gid = RandomUtil.randomString(6);
        } while (hasGid(gid,username));

        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                .username(username)
                .sortOrder(0)
                .build();
        boolean isSuccess = save(groupDO);
        if(!isSuccess){
            throw new ClientException(GROUP_SAVE_ERROR);
        }
    }

    @Override
    public List<GroupRespDTO> getGroups() {
        String username = UserHolder.getUser().getUsername();
        List<GroupDO> groupDOList = query().eq("username", username).eq("del_flag",0).orderByDesc("sortOrder", "updateTime").list();
        return BeanUtil.copyToList(groupDOList, GroupRespDTO.class);
    }

    /**
     *
     * 判断gid是否存在
     */
    private boolean hasGid(String gid, String username){
        GroupDO groupDO = query().eq("gid", gid).eq("del_flag",0).eq("username", username).one();
        return groupDO != null;
    }
}
