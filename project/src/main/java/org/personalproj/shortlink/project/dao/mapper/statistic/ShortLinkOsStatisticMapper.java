package org.personalproj.shortlink.project.dao.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkOsStatisticDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;

import java.util.List;

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

    /**
     *
     * 根据操作系统分类统计每个操作系统的点击数
     */
    @Select(
            "SELECT " +
            "    os, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_os_statistic " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, os;"
    )
    List<ShortLinkOsStatisticDO> listOsStatsByShortLink(@Param("param")ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     *
     * 根据操作系统分类统计每个短链接组每个操作系统的点击数
     */
    @Select(
            "SELECT " +
            "    os, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_os_statistic " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, os;"
    )
    List<ShortLinkOsStatisticDO> listOsStatsByShortLinkGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
