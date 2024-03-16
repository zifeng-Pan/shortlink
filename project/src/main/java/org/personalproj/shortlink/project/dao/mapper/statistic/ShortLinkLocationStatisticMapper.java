package org.personalproj.shortlink.project.dao.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkLocationStatisticDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;

import java.util.List;

/**
 *
 * 短链接地区统计持久层
 */
public interface ShortLinkLocationStatisticMapper extends BaseMapper<ShortLinkLocationStatisticDO> {

    /**
     * 记录地区访问监控数据
     */
    @Insert("INSERT INTO t_link_location_statistic (full_short_url, gid, date, cnt, country, province, city, adcode, create_time, update_time, del_flag) " +
            "VALUES( #{linkLocaleStats.fullShortUrl}, #{linkLocaleStats.gid}, #{linkLocaleStats.date}, #{linkLocaleStats.cnt}, #{linkLocaleStats.country}, #{linkLocaleStats.province}, #{linkLocaleStats.city}, #{linkLocaleStats.adcode}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkLocaleStats.cnt};")
    void shortLinkLocaleState(@Param("linkLocaleStats") ShortLinkLocationStatisticDO shortLinkLocationStatisticDO);

    /**
     *
     * 根据省份分类统计每一个短链接在每个省下面的点击总数
     */
    @Select("SELECT " +
            "    province, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_location_statistic " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, province;")
    List<ShortLinkLocationStatisticDO> listLocalStatsByShortLink(@Param("param")ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     *
     * 根据省份分类统计每一个短链接组在每个省下面的点击总数
     */
    @Select("SELECT " +
            "    province, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_location_statistic " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, province;")
    List<ShortLinkLocationStatisticDO> listLocalStatsByShortLinkGroup(@Param("param") ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
