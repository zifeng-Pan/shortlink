package org.personalproj.shortlink.project.dao.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkLocationStatisticDO;

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
}
