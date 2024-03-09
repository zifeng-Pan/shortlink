package org.personalproj.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.dao.entity.GroupDO;
import org.personalproj.shortlink.admin.dao.mapper.GroupMapper;
import org.personalproj.shortlink.admin.dto.req.GroupSortReqDTO;
import org.personalproj.shortlink.admin.dto.req.GroupUpdateReqDTO;
import org.personalproj.shortlink.admin.dto.resp.GroupRespDTO;
import org.personalproj.shortlink.admin.service.GroupService;
import org.personalproj.shortlink.admin.toolkit.UserHolder;
import org.personalproj.shortlink.common.convention.exception.ClientException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.personalproj.shortlink.admin.common.enums.GroupErrorCode.GROUP_SAVE_ERROR;
import static org.personalproj.shortlink.admin.common.enums.GroupErrorCode.GROUP_UPDATE_ERROR;

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
    @Transactional(rollbackFor = {RuntimeException.class})
    public void saveGroup(String groupName){
        this.saveGroup(groupName,UserHolder.getUser().getUsername());
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void saveGroup(String groupName, String username) {
        String gid;
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
        List<GroupDO> groupDOList = query().eq("username", username).eq("del_flag",0).orderByDesc("sort_order", "update_time").list();
        return BeanUtil.copyToList(groupDOList, GroupRespDTO.class);
    }


    /**
     *
     * 短链接组属性更新
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void updateGroup(GroupUpdateReqDTO groupUpdateReqDTO) {
        GroupDO groupDO = new GroupDO();
        groupDO.setName(groupUpdateReqDTO.getName());
        groupDO.setUpdateTime(new Date());
        boolean isSuccess = update(groupDO, Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserHolder.getUser().getUsername())
                .eq(GroupDO::getGid, groupUpdateReqDTO.getGid())
                .eq(GroupDO::getDelFlag, 0)
        );
        if(!isSuccess){
            throw new ClientException(GROUP_UPDATE_ERROR);
        }
    }


    /**
     *
     * 短链接组删除
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void removeGroup(String gid) {
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        boolean isSuccess = update(groupDO, Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserHolder.getUser().getUsername())
                .eq(GroupDO::getDelFlag, 0)
        );
        if(!isSuccess){
            throw new ClientException(GROUP_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void sortOrderUpdate(List<GroupSortReqDTO> groupSortReqDTOList) {
        groupSortReqDTOList.forEach(groupSortReqDTO -> {
            GroupDO groupDO = GroupDO.builder().gid(groupSortReqDTO.getGid()).sortOrder(groupSortReqDTO.getSortOrder()).build();
            boolean isSuccess = update(groupDO, Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getGid, groupDO.getGid())
                    .eq(GroupDO::getDelFlag, 0)
                    .eq(GroupDO::getUsername, UserHolder.getUser().getUsername())
            );
            if(!isSuccess){
                throw new ClientException(GROUP_UPDATE_ERROR);
            }
        });
    }


    /**
     *
     * 判断gid是否存在
     */
    private boolean hasGid(String gid, String username){
        GroupDO groupDO = query()
                .eq("gid", gid)
                .eq("del_flag",0)
                .eq("username", username).one();
        return groupDO != null;
    }
}
