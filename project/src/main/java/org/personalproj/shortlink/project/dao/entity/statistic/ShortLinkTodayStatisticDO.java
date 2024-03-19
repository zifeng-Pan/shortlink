package org.personalproj.shortlink.project.dao.entity.statistic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.personalproj.shortlink.common.database.BaseDO;

import java.util.Date;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.dao.entity.statistic
 * @Author: PzF
 * @CreateTime: 2024-03-19  09:23
 * @Description: 短链接当日统计
 * @Version: 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_stats_today")
public class ShortLinkTodayStatisticDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 今日pv
     */
    private Integer todayPv;

    /**
     * 今日uv
     */
    private Integer todayUv;

    /**
     * 今日ip数
     */
    private Integer todayUip;
}
