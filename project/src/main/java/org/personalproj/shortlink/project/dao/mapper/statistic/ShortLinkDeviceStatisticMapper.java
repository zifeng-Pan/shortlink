package org.personalproj.shortlink.project.dao.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkDeviceStatisticDO;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;

import java.util.List;

/**
 *
 * 短链接设备统计持久层
 */
public interface ShortLinkDeviceStatisticMapper extends BaseMapper<ShortLinkDeviceStatisticDO> {
    /**
     * 记录访问设备监控数据
     */
    @Insert("INSERT INTO t_link_device_statistic (full_short_url, gid, date, cnt, device, create_time, update_time, del_flag) " +
            "VALUES( #{linkDeviceStats.fullShortUrl}, #{linkDeviceStats.gid}, #{linkDeviceStats.date}, #{linkDeviceStats.cnt}, #{linkDeviceStats.device}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkDeviceStats.cnt};")
    void shortLinkDeviceState(@Param("linkDeviceStats") ShortLinkDeviceStatisticDO shortLinkDeviceStatisticDO);

    @Select("SELECT " +
            "    device, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_device_statistic " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, device;")
    List<ShortLinkDeviceStatisticDO> listDeviceStatsByShortLink(@Param("param") ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    @Select("SELECT " +
            "    device, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_device_statistic " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, device;")
    List<ShortLinkDeviceStatisticDO> listDeviceStatsByShortLinkGroup(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO);
}
