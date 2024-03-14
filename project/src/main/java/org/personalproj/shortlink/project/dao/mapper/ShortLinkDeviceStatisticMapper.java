package org.personalproj.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.personalproj.shortlink.project.dao.entity.ShortLinkDeviceStatisticDO;

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
}
