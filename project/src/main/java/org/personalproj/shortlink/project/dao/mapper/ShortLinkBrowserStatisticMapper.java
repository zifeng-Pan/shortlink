package org.personalproj.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.personalproj.shortlink.project.dao.entity.ShortLinkBrowserStatisticDO;


/**
 *
 * 短链接访问浏览器统计持久层
 */
public interface ShortLinkBrowserStatisticMapper extends BaseMapper<ShortLinkBrowserStatisticDO> {
    /**
     * 记录浏览器访问监控数据
     */
    @Insert("INSERT INTO t_link_browser_statistic (full_short_url, gid, date, cnt, browser, create_time, update_time, del_flag) " +
            "VALUES( #{linkBrowserStats.fullShortUrl}, #{linkBrowserStats.gid}, #{linkBrowserStats.date}, #{linkBrowserStats.cnt}, #{linkBrowserStats.browser}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkBrowserStats.cnt};")
    void shortLinkBrowserState(@Param("linkBrowserStats") ShortLinkBrowserStatisticDO shortLinkBrowserStatisticDO);

}
