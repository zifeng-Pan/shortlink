package org.personalproj.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.admin.dao.entity.GroupDO;
import org.personalproj.shortlink.admin.dao.mapper.GroupMapper;
import org.personalproj.shortlink.admin.service.GroupService;
import org.springframework.stereotype.Service;

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
}
