package org.personalproj.shortlink.project.dao.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkAccessLogsDO;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkStatisticDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;

import java.util.HashMap;
import java.util.List;

/**
 *
 * 短链接访问日志持久层
 */
public interface ShortLinkAccessLogsMapper extends BaseMapper<ShortLinkAccessLogsDO> {

    /**
     * 根据短链接获取指定日期内PV、UV、UIP数据
     */
    @Select("SELECT " +
            "    COUNT(user) AS pv, " +
            "    COUNT(DISTINCT user) AS uv, " +
            "    COUNT(DISTINCT ip) AS uip " +
            "FROM " +
            "    t_link_access_logs " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid;")
    ShortLinkStatisticDO calUvPvUipByShortLink(@Param("param")ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 根据短链接获取指定日期内PV、UV、UIP数据
     */
    @Select("SELECT " +
            "    COUNT(user) AS pv, " +
            "    COUNT(DISTINCT user) AS uv, " +
            "    COUNT(DISTINCT ip) AS uip " +
            "FROM " +
            "    t_link_access_logs " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid;")
    ShortLinkStatisticDO calUvPvUipByShortLinkGroup(@Param("param") ShortLinkGroupStatsReqDTO ShortLinkGroupStatsReqDTO);

    /**
     * 查询时间范围内访问短链接的用户类型
     */
    @Select("SELECT " +
            "COUNT(DISTINCT case when t2.`user` is NOT NULL THEN t2.`user` END) as oldUserCount," +
            "COUNT(DISTINCT case when t2.`user` is null THEN t1.`user` END) as newUserCount " +
            " FROM " +
            " (SELECT user, min(date) as now_first_access_time from t_link_access_logs WHERE date >= #{param.startDate} And date <= #{param.endDate} GROUP BY user)  as t1" +
            " LEFT JOIN " +
            "(select user ,date from t_link_access_logs WHERE date <  #{param.startDate}) as t2" +
            " ON t1.user = t2.user;")
    HashMap<String, Object> userTypeCountByShortLink(@Param("param") ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 查询时间范围内访问所有短链接组中的短链接的用户类型
     */
    @Select("SELECT " +
            "COUNT(DISTINCT case when t2.`gid` is NOT NULL THEN t2.`gid` END) as oldUserCount, " +
            "COUNT(DISTINCT case when t2.`gid` is null THEN t1.`gid` END) as newUserCount " +
            " FROM " +
            "(SELECT gid, min(date) as now_first_access_time from t_link_access_logs WHERE date >= #{param.startDate} And date <= #{param.endDate} GROUP BY gid)  as t1" +
            " left JOIN " +
            "(select gid ,date from t_link_access_logs WHERE date <  #{param.startDate}) as t2" +
            " ON t1.gid = t2.gid;")
    HashMap<String, Object> userTypeCountByShortLinkGroup(@Param("param") ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);

    /**
     * 根据短链接获取指定日期内高频访问IP数据
     */
    @Select("SELECT " +
            "    ip, " +
            "    COUNT(ip) AS count " +
            "FROM " +
            "    t_link_access_logs " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, ip " +
            "ORDER BY " +
            "    count DESC " +
            "LIMIT 5;")
    List<HashMap<String, Object>> listTopIpByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接组获取指定日期内高频访问IP数据
     */
    @Select("SELECT " +
            "    ip, " +
            "    COUNT(ip) AS count " +
            "FROM " +
            "    t_link_access_logs " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, ip " +
            "ORDER BY " +
            "    count DESC " +
            "LIMIT 5;")
    List<HashMap<String, Object>> listTopIpByShortLinkGroup(@Param("param") ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
