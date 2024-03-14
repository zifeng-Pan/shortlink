package org.personalproj.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.personalproj.shortlink.project.dao.entity.ShortLinkStatisticDO;


/**
 *
 * 短链接基础访问实体类对应Mapper
 */
public interface ShortLinkStatisticMapper extends BaseMapper<ShortLinkStatisticDO> {
    /**
     *
     * 记录基础访问监控数据
     */
    @Insert("INSERT INTO t_link_access_stats ( full_short_url, gid, date, pv, uv, uip, HOUR, weekday, create_time, update_time, del_flag )" +
            "VALUES(#{shortLinkStatistic.fullShortUrl}, #{shortLinkStatistic.gid}, #{shortLinkStatistic.gid}, #{shortLinkStatistic.date}, #{shortLinkStatistic.pv}, #{shortLinkStatistic.uv}, #{shortLinkStatistic.uip}, #{shortLinkStatistic.hour}, #{shortLinkStatistic.weekday}, NOW(), NOW(), 0)" +
            "ON DUPLICATE KEY UPDATE pv = pv + #{shortLinkStatistic.pv}, uv = uv + #{shortLinkStatistic.uv}, uip = uip + #{shortLinkStatistic.uip};")
    void shortLinkStatisticInsert(@Param("shortLinkStatistic") ShortLinkStatisticDO shortLinkStatisticDO);
}
