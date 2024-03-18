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
 * @CreateTime: 2024-03-13  09:25
 * @Description: 短链接基础访问统计实体[具体到日期，星期，小时内的uv,pv,uip]
 * @Version: 1.0
 */
@Data
@Builder
@TableName("t_link_statistic")
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatisticDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 所属短链接组的gid
     */
    private String gid;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer pv;

    /**
     * 独立访客数
     */
    private Integer uv;

    /**
     * 独立ip数
     */
    private Integer uip;

    /**
     * 小时
     */
    private Integer hour;

    /**
     * 星期
     */
    private Integer weekday;
}
