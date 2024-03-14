package org.personalproj.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.personalproj.shortlink.project.dao.entity.ShortLinkOsStatisticDO;

/**
 *
 * 短链接操作系统持久层
 */
public interface ShortLinkOsStatisticMapper extends BaseMapper<ShortLinkOsStatisticDO> {
    /**
     * 记录地区访问监控数据
     */
    @Insert("INSERT INTO t_link_os_statistic (full_short_url, gid, date, cnt, os, create_time, update_time, del_flag) " +
            "VALUES( #{linkOsStats.fullShortUrl}, #{linkOsStats.gid}, #{linkOsStats.date}, #{linkOsStats.cnt}, #{linkOsStats.os}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkOsStats.cnt};")
    void shortLinkOsState(@Param("linkOsStats") ShortLinkOsStatisticDO shortLinkOsStatisticDO);
}
