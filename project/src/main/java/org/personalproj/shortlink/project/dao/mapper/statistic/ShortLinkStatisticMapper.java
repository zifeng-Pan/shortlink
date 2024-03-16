package org.personalproj.shortlink.project.dao.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkStatisticDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;

import java.util.List;


/**
 *
 * 短链接基础访问实体类对应Mapper
 */
public interface ShortLinkStatisticMapper extends BaseMapper<ShortLinkStatisticDO> {
    /**
     *
     * 记录基础访问监控数据
     */
    @Insert("INSERT INTO t_link_statistic ( full_short_url, gid, date, pv, uv, uip, HOUR, weekday, create_time, update_time, del_flag )" +
            "VALUES(#{shortLinkStatistic.fullShortUrl}, #{shortLinkStatistic.gid}, #{shortLinkStatistic.gid}, #{shortLinkStatistic.date}, #{shortLinkStatistic.pv}, #{shortLinkStatistic.uv}, #{shortLinkStatistic.uip}, #{shortLinkStatistic.hour}, #{shortLinkStatistic.weekday}, NOW(), NOW(), 0)" +
            "ON DUPLICATE KEY UPDATE pv = pv + #{shortLinkStatistic.pv}, uv = uv + #{shortLinkStatistic.uv}, uip = uip + #{shortLinkStatistic.uip};")
    void shortLinkStatisticInsert(@Param("shortLinkStatistic") ShortLinkStatisticDO shortLinkStatisticDO);

    /**
     *
     * 时间段内每一个日期内每一个短链接的uv，pv，uip总数统计
     */
    @Select("SELECT " +
            "    date, " +
            "    SUM(pv) AS pv, " +
            "    SUM(uv) AS uv, " +
            "    SUM(uip) AS uip " +
            "FROM " +
            "    t_link_statistic " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, date;")
    List<ShortLinkStatisticDO> listShortLinkBaseStatistic(@Param("param")ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     *
     * 时间段内每一个日期内每一个分组内部的uv，pv，uip总数统计
     */
    @Select("SELECT " +
            "    date, " +
            "    SUM(pv) AS pv, " +
            "    SUM(uv) AS uv, " +
            "    SUM(uip) AS uip " +
            "FROM " +
            "    t_link_statistic " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "     gid, date;")
    List<ShortLinkStatisticDO> listShortLinkBaseStatisticByGroup(@Param("param") ShortLinkGroupStatsReqDTO ShortLinkGroupStatsReqDTO);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    weekday, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, weekday;")
    List<ShortLinkStatisticDO> listWeekdayStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    hour, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, hour;")
    List<ShortLinkStatisticDO> listHourStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);


    /**
     * 根据短链接组获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    hour, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, hour;")
    List<ShortLinkStatisticDO> listWeekdayStatsByShortLinkGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);

    /**
     * 根据短链接组获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    hour, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, hour;")
    List<ShortLinkStatisticDO> listHourStatsByShortLinkGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
