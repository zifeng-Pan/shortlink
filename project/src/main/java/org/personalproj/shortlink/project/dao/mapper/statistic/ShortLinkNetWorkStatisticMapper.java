package org.personalproj.shortlink.project.dao.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.personalproj.shortlink.project.dao.entity.statistic.ShortLinkNetWorkStatisticDO;

/**
 *
 * 短链接监控之网络监控持久层
 */
public interface ShortLinkNetWorkStatisticMapper extends BaseMapper<ShortLinkNetWorkStatisticDO> {
    /**
     * 记录访问设备监控数据
     */
    @Insert("INSERT INTO t_link_network_statistic (full_short_url, gid, date, cnt, network, create_time, update_time, del_flag) " +
            "VALUES( #{linkNetworkStats.fullShortUrl}, #{linkNetworkStats.gid}, #{linkNetworkStats.date}, #{linkNetworkStats.cnt}, #{linkNetworkStats.network}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkNetworkStats.cnt};")
    void shortLinkNetworkState(@Param("linkNetworkStats") ShortLinkNetWorkStatisticDO ShortLinkNetWorkStatisticDO);
}