package org.personalproj.shortlink.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.personalproj.shortlink.project.dao.entity.statistic.*;
import org.personalproj.shortlink.project.dao.mapper.statistic.*;
import org.personalproj.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.personalproj.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.personalproj.shortlink.project.dto.resp.stats.*;
import org.personalproj.shortlink.project.service.ShortLinkStatisticService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.service.impl
 * @Author: PzF
 * @CreateTime: 2024-03-14  18:55
 * @Description: 短链接统计服务实现层
 * @Version: 1.0
 */

@Service
@RequiredArgsConstructor
public class ShortLinkStatisticServiceImpl extends ServiceImpl<ShortLinkStatisticMapper, ShortLinkStatisticDO> implements ShortLinkStatisticService {

    private final ShortLinkStatisticMapper shortLinkStatisticMapper;

    private final ShortLinkOsStatisticMapper shortLinkOsStatisticMapper;

    private final ShortLinkLocationStatisticMapper shortLinkLocationStatisticMapper;

    private final ShortLinkBrowserStatisticMapper shortLinkBrowserStatisticMapper;

    private final ShortLinkAccessLogsMapper shortLinkAccessLogsMapper;

    private final ShortLinkDeviceStatisticMapper shortLinkDeviceStatisticMapper;

    private final ShortLinkNetWorkStatisticMapper shortLinkNetWorkStatisticMapper;

    /*
    private <R,T> R reflectMapperMethodInvoke(BaseMapper<T> baseMapper, Class<R> returnClass, String methodType,  T requestParams, Class<T> paramClass){
        return null;
    }
     */

    @Override
    public ShortLinkStatsRespDTO singeShortLinkStatistic(ShortLinkStatsReqDTO shortLinkStatsReqDTO) {
        // 1.根据起止日期查询出这段时间的基础统计数据，构建每日短链接基础访问统计实体，同时汇总的uv，pv,uip
        // 1.1 查询出基础统计表中当前短链接每一天的uv，uip，pv总数
        List<ShortLinkStatisticDO> shortLinkBaseDailyStatisticList = baseMapper.listShortLinkBaseStatistic(shortLinkStatsReqDTO);
        if(CollectionUtil.isEmpty(shortLinkBaseDailyStatisticList)){
            return null;
        }
        // 1.2 该段时间内uv，uip，pv的总数统计
        ShortLinkStatisticDO shortLinkStatisticDO = shortLinkAccessLogsMapper.calUvPvUipByShortLink(shortLinkStatsReqDTO);
        // 1.3 根据列表进行顺序匹配，日期在起始日期内的，进行设置，否则新建一个为0的该短链接每日统计
        List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
        List<String> rangeList = DateUtil.rangeToList(DateUtil.parse(shortLinkStatsReqDTO.getStartDate()), DateUtil.parse(shortLinkStatsReqDTO.getEndDate()), DateField.DAY_OF_MONTH).stream()
                .map(DateUtil::formatDate)
                .toList();
        rangeList.forEach(date ->{
                shortLinkBaseDailyStatisticList.stream()
                        .filter(item -> Objects.equals(DateUtil.formatDate(item.getDate()),date))
                        .findFirst()
                        .ifPresentOrElse(item -> {
                            ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                                    .date(date)
                                    .pv(item.getPv())
                                    .uv(item.getUv())
                                    .uip(item.getUip())
                                    .build();
                            daily.add(accessDailyRespDTO);
                        },() ->{
                            ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                                    .date(date)
                                    .pv(0)
                                    .uv(0)
                                    .uip(0)
                                    .build();
                            daily.add(accessDailyRespDTO);
                        });
            }
        );
        // 2.时间段内的地区，操作系统，访问设备类型，访问网络类型,浏览器短链接访问统计实体构建，并计算该时间段内的占比
        // 2.1 时间段内地区数据统计
        List<ShortLinkStatsLocaleCNRespDTO> locationStatisticsList = new ArrayList<>();
        List<ShortLinkLocationStatisticDO> listedLocaleByShortLink  = shortLinkLocationStatisticMapper.listLocalStatsByShortLink(shortLinkStatsReqDTO);
        int localCntSum = listedLocaleByShortLink.stream().mapToInt(ShortLinkLocationStatisticDO::getCnt).sum();
        listedLocaleByShortLink.forEach(item ->{
            double ratio = (double) item.getCnt() / localCntSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsLocaleCNRespDTO cnRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
                    .locale(item.getProvince())
                    .ratio(actualRatio)
                    .cnt(item.getCnt())
                    .build();
            locationStatisticsList.add(cnRespDTO);
        });

        // 2.2 时间段内操作系统分布统计
        List<ShortLinkStatsOsRespDTO> osStatisticList = new ArrayList<>();
        List<ShortLinkOsStatisticDO> listedOsByShortLink = shortLinkOsStatisticMapper.listOsStatsByShortLink(shortLinkStatsReqDTO);
        int osCntSum = listedOsByShortLink.stream().mapToInt(ShortLinkOsStatisticDO::getCnt).sum();
        listedOsByShortLink.forEach(item ->{
            double ratio = (double) item.getCnt() / osCntSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
                    .os(item.getOs())
                    .ratio(actualRatio)
                    .cnt(item.getCnt())
                    .build();
            osStatisticList.add(osRespDTO);
        });

        // 2.3 访问设备类型分布统计
        List<ShortLinkStatsDeviceRespDTO> deviceStatisticList = new ArrayList<>();
        List<ShortLinkDeviceStatisticDO> listedDeviceByShortLink = shortLinkDeviceStatisticMapper.listDeviceStatsByShortLink(shortLinkStatsReqDTO);
        int deviceCntSum = listedDeviceByShortLink.stream().mapToInt(ShortLinkDeviceStatisticDO::getCnt).sum();
        listedDeviceByShortLink.forEach(item ->{
            double ratio = (double) item.getCnt() / deviceCntSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                    .device(item.getDevice())
                    .ratio(actualRatio)
                    .cnt(item.getCnt())
                    .build();
            deviceStatisticList.add(deviceRespDTO);
        });

        // 2.4 访问浏览器类型分布统计
        List<ShortLinkStatsBrowserRespDTO> browserStatisticList = new ArrayList<>();
        List<HashMap<String, Object>> listBrowserStatsByShortLink = shortLinkBrowserStatisticMapper.listBrowserStatsByShortLink(shortLinkStatsReqDTO);
        int browserSum = listBrowserStatsByShortLink.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        listBrowserStatsByShortLink.forEach(each -> {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / browserSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .browser(each.get("browser").toString())
                    .ratio(actualRatio)
                    .build();
            browserStatisticList.add(browserRespDTO);
        });

        // 2.5访问网络类型详情
        List<ShortLinkStatsNetworkRespDTO> networkStatisticList = new ArrayList<>();
        List<ShortLinkNetWorkStatisticDO> listNetworkStatsByShortLink = shortLinkNetWorkStatisticMapper.listNetworkStatsByShortLink(shortLinkStatsReqDTO);
        int networkSum = listNetworkStatsByShortLink.stream()
                .mapToInt(ShortLinkNetWorkStatisticDO::getCnt)
                .sum();
        listNetworkStatsByShortLink.forEach(each -> {
            double ratio = (double) each.getCnt() / networkSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                    .cnt(each.getCnt())
                    .network(each.getNetwork())
                    .ratio(actualRatio)
                    .build();
            networkStatisticList.add(networkRespDTO);
        });

        // 3.访客类型实体构建，主要是通过分组查询之前是否访问过
        List<ShortLinkStatsUserTypeRespDTO> userTypeStatisticList = new ArrayList<>();
        HashMap<String, Object> userTypeCountMap = shortLinkAccessLogsMapper.userTypeCountByShortLink(shortLinkStatsReqDTO);
        int oldUserCnt = Integer.parseInt(
                Optional.ofNullable(userTypeCountMap)
                        .map(each -> each.get("oldUserCount"))
                        .map(Object::toString)
                        .orElse("0")
        );
        int newUserCnt = Integer.parseInt(
                Optional.ofNullable(userTypeCountMap)
                        .map(each -> each.get("newUserCount"))
                        .map(Object::toString)
                        .orElse("0")
        );
        int uvSum = oldUserCnt + newUserCnt;
        double oldRatio = (double) oldUserCnt / uvSum;
        double actualOldRatio = Math.round(oldRatio * 100.0) / 100.0;
        double newRatio = (double) newUserCnt / uvSum;
        double actualNewRatio = Math.round(newRatio * 100.0) / 100.0;

        ShortLinkStatsUserTypeRespDTO newUvRespDTO = ShortLinkStatsUserTypeRespDTO.builder()
                .uvType("newUser")
                .cnt(newUserCnt)
                .ratio(actualNewRatio)
                .build();
        userTypeStatisticList.add(newUvRespDTO);
        ShortLinkStatsUserTypeRespDTO oldUvRespDTO = ShortLinkStatsUserTypeRespDTO.builder()
                .uvType("oldUser")
                .cnt(oldUserCnt)
                .ratio(actualOldRatio)
                .build();
        userTypeStatisticList.add(oldUvRespDTO);
        // 4.从星期一至星期日时间访问分布统计，以及访问小时分布统计
        // 4.1 星期一至星期日时间访问分布统计
        List<ShortLinkStatisticDO> listWeekdayStatsByShortLink = shortLinkStatisticMapper.listWeekdayStatsByShortLink(shortLinkStatsReqDTO);
        int[] weekDayStatistic = new int[7];
        for(ShortLinkStatisticDO statisticDO : listWeekdayStatsByShortLink){
            weekDayStatistic[statisticDO.getWeekday() - 1] += statisticDO.getPv();
        }
        // 4.2 访问小时分布统计
        List<ShortLinkStatisticDO> listHourStatsByShortLink = shortLinkStatisticMapper.listHourStatsByShortLink(shortLinkStatsReqDTO);
        int[] hourStatistic = new int[24];
        for(ShortLinkStatisticDO statisticDO : listHourStatsByShortLink){
            hourStatistic[statisticDO.getHour()] += statisticDO.getPv();
        }
        // 5.高频ip统计
        List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
        List<HashMap<String, Object>> listTopIpByShortLink = shortLinkAccessLogsMapper.listTopIpByShortLink(shortLinkStatsReqDTO);
        listTopIpByShortLink.forEach(each -> {
            ShortLinkStatsTopIpRespDTO statsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                    .ip(each.get("ip").toString())
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .build();
            topIpStats.add(statsTopIpRespDTO);
        });
        return ShortLinkStatsRespDTO.builder()
                .pv(shortLinkStatisticDO.getPv())
                .uv(shortLinkStatisticDO.getUv())
                .uip(shortLinkStatisticDO.getUip())
                .daily(daily)
                .localeCnStats(locationStatisticsList)
                .hourStats(hourStatistic)
                .topIpStats(topIpStats)
                .weekdayStats(weekDayStatistic)
                .browserStats(browserStatisticList)
                .osStats(osStatisticList)
                .uvTypeStats(userTypeStatisticList)
                .deviceStats(deviceStatisticList)
                .networkStats(networkStatisticList)
                .build();
    }


    @Override
    public ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO shortLinkGroupStatsReqDTO) {
        // 1.根据起止日期查询出这段时间的基础统计数据，构建每日组内短链接基础访问统计实体，同时汇总组内所有短链接的uv，pv,uip
        // 1.1 查询出基础统计表中当前短链接组每一天的uv，uip，pv总数
        List<ShortLinkStatisticDO> shortLinkBaseDailyStatisticByGroupList = baseMapper.listShortLinkBaseStatisticByGroup(shortLinkGroupStatsReqDTO);
        // 如果没有查出对应短链接组统计数据
        if (CollectionUtil.isEmpty(shortLinkBaseDailyStatisticByGroupList)) {
            return null;
        }
        // 短链接组的uv，pv，uip统计
        ShortLinkStatisticDO groupUvPvUipStatisticDO = shortLinkAccessLogsMapper.calUvPvUipByShortLinkGroup(shortLinkGroupStatsReqDTO);
        // 基础访问统计
        List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
        List<String> rangeDates = DateUtil.rangeToList(DateUtil.parse(shortLinkGroupStatsReqDTO.getStartDate()), DateUtil.parse(shortLinkGroupStatsReqDTO.getEndDate()), DateField.DAY_OF_MONTH).stream()
                .map(DateUtil::formatDate)
                .toList();
        rangeDates.forEach(dateStr -> shortLinkBaseDailyStatisticByGroupList
                .stream()
                .filter(item -> Objects.equals(dateStr, DateUtil.formatDate(item.getDate())))
                .findFirst()
                .ifPresentOrElse(item -> {
                            ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                                    .date(dateStr)
                                    .pv(item.getPv())
                                    .uv(item.getUv())
                                    .uip(item.getUip())
                                    .build();
                            daily.add(accessDailyRespDTO);
                        }, () -> {
                            ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                                    .date(dateStr)
                                    .pv(0)
                                    .uv(0)
                                    .uip(0)
                                    .build();
                            daily.add(accessDailyRespDTO);
                        }
                )
        );

        // 2.时间段内的地区，操作系统，访问设备类型，访问网络类型,浏览器短链接访问统计实体构建，并计算该时间段内的占比
        // 2.1 时间段内地区数据统计
        List<ShortLinkStatsLocaleCNRespDTO> locationGroupStatisticsList = new ArrayList<>();
        List<ShortLinkLocationStatisticDO> listedLocaleByShortLink = shortLinkLocationStatisticMapper.listLocalStatsByShortLinkGroup(shortLinkGroupStatsReqDTO);
        int localCntSum = listedLocaleByShortLink.stream().mapToInt(ShortLinkLocationStatisticDO::getCnt).sum();
        listedLocaleByShortLink.forEach(item -> {
            double ratio = (double) item.getCnt() / localCntSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsLocaleCNRespDTO cnRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
                    .locale(item.getProvince())
                    .ratio(actualRatio)
                    .cnt(item.getCnt())
                    .build();
            locationGroupStatisticsList.add(cnRespDTO);
        });

        // 2.2 时间段内操作系统分布统计
        List<ShortLinkStatsOsRespDTO> osGroupStatisticList = new ArrayList<>();
        List<ShortLinkOsStatisticDO> listedOsByShortLink = shortLinkOsStatisticMapper.listOsStatsByShortLinkGroup(shortLinkGroupStatsReqDTO);
        int osCntSum = listedOsByShortLink.stream().mapToInt(ShortLinkOsStatisticDO::getCnt).sum();
        listedOsByShortLink.forEach(item -> {
            double ratio = (double) item.getCnt() / osCntSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
                    .os(item.getOs())
                    .ratio(actualRatio)
                    .cnt(item.getCnt())
                    .build();
            osGroupStatisticList.add(osRespDTO);
        });

        // 2.3 访问设备类型分布统计
        List<ShortLinkStatsDeviceRespDTO> deviceGroupStatisticList = new ArrayList<>();
        List<ShortLinkDeviceStatisticDO> listedDeviceByShortLink = shortLinkDeviceStatisticMapper.listDeviceStatsByShortLinkGroup(shortLinkGroupStatsReqDTO);
        int deviceCntSum = listedDeviceByShortLink.stream().mapToInt(ShortLinkDeviceStatisticDO::getCnt).sum();
        listedDeviceByShortLink.forEach(item -> {
            double ratio = (double) item.getCnt() / deviceCntSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                    .device(item.getDevice())
                    .ratio(actualRatio)
                    .cnt(item.getCnt())
                    .build();
            deviceGroupStatisticList.add(deviceRespDTO);
        });

        // 2.4 访问浏览器类型分布统计
        List<ShortLinkStatsBrowserRespDTO> browserGroupStatisticList = new ArrayList<>();
        List<HashMap<String, Object>> listBrowserStatsByShortLink = shortLinkBrowserStatisticMapper.listBrowserStatsByShortLinkGroup(shortLinkGroupStatsReqDTO);
        int browserSum = listBrowserStatsByShortLink.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        listBrowserStatsByShortLink.forEach(each -> {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / browserSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .browser(each.get("browser").toString())
                    .ratio(actualRatio)
                    .build();
            browserGroupStatisticList.add(browserRespDTO);
        });

        // 2.5访问网络类型详情
        List<ShortLinkStatsNetworkRespDTO> networkGroupStatisticList = new ArrayList<>();
        List<ShortLinkNetWorkStatisticDO> listNetworkStatsByShortLink = shortLinkNetWorkStatisticMapper.listNetworkStatsByShortLinkGroup(shortLinkGroupStatsReqDTO);
        int networkSum = listNetworkStatsByShortLink.stream()
                .mapToInt(ShortLinkNetWorkStatisticDO::getCnt)
                .sum();
        listNetworkStatsByShortLink.forEach(each -> {
            double ratio = (double) each.getCnt() / networkSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                    .cnt(each.getCnt())
                    .network(each.getNetwork())
                    .ratio(actualRatio)
                    .build();
            networkGroupStatisticList.add(networkRespDTO);
        });

        // 3.访客类型实体构建，主要是通过分组查询之前是否访问过
        List<ShortLinkStatsUserTypeRespDTO> userTypeGroupStatisticList = new ArrayList<>();
        HashMap<String, Object> userTypeCountMap = shortLinkAccessLogsMapper.userTypeCountByShortLinkGroup(shortLinkGroupStatsReqDTO);
        int oldUserCnt = Integer.parseInt(
                Optional.ofNullable(userTypeCountMap)
                        .map(each -> each.get("oldUserCount"))
                        .map(Object::toString)
                        .orElse("0")
        );
        int newUserCnt = Integer.parseInt(
                Optional.ofNullable(userTypeCountMap)
                        .map(each -> each.get("newUserCount"))
                        .map(Object::toString)
                        .orElse("0")
        );
        int uvSum = oldUserCnt + newUserCnt;
        double oldRatio = (double) oldUserCnt / uvSum;
        double actualOldRatio = Math.round(oldRatio * 100.0) / 100.0;
        double newRatio = (double) newUserCnt / uvSum;
        double actualNewRatio = Math.round(newRatio * 100.0) / 100.0;

        ShortLinkStatsUserTypeRespDTO newUvRespDTO = ShortLinkStatsUserTypeRespDTO.builder()
                .uvType("newUser")
                .cnt(newUserCnt)
                .ratio(actualNewRatio)
                .build();
        userTypeGroupStatisticList.add(newUvRespDTO);
        ShortLinkStatsUserTypeRespDTO oldUvRespDTO = ShortLinkStatsUserTypeRespDTO.builder()
                .uvType("oldUser")
                .cnt(oldUserCnt)
                .ratio(actualOldRatio)
                .build();
        userTypeGroupStatisticList.add(oldUvRespDTO);
        // 4.从星期一至星期日时间访问分布统计，以及访问小时分布统计
        // 4.1 星期一至星期日时间访问分布统计
        List<ShortLinkStatisticDO> listWeekdayStatsByShortLinkGroup = shortLinkStatisticMapper.listWeekdayStatsByShortLinkGroup(shortLinkGroupStatsReqDTO);
        int[] weekDayStatistic = new int[7];
        for (ShortLinkStatisticDO statisticDO : listWeekdayStatsByShortLinkGroup) {
            weekDayStatistic[statisticDO.getWeekday() - 1] += statisticDO.getPv();
        }
        // 4.2 访问小时分布统计
        List<ShortLinkStatisticDO> listHourStatsByShortLinkGroup = shortLinkStatisticMapper.listHourStatsByShortLinkGroup(shortLinkGroupStatsReqDTO);
        int[] hourStatistic = new int[24];
        for (ShortLinkStatisticDO statisticDO : listHourStatsByShortLinkGroup) {
            hourStatistic[statisticDO.getHour()] += statisticDO.getPv();
        }
        // 5.高频ip统计
        List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
        List<HashMap<String, Object>> listTopIpByShortLinkGroup = shortLinkAccessLogsMapper.listTopIpByShortLinkGroup(shortLinkGroupStatsReqDTO);
        listTopIpByShortLinkGroup.forEach(each -> {
            ShortLinkStatsTopIpRespDTO statsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                    .ip(each.get("ip").toString())
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .build();
            topIpStats.add(statsTopIpRespDTO);
        });

        return ShortLinkStatsRespDTO.builder()
                .uv(groupUvPvUipStatisticDO.getUv())
                .pv(groupUvPvUipStatisticDO.getPv())
                .uip(groupUvPvUipStatisticDO.getUip())
                .networkStats(networkGroupStatisticList)
                .localeCnStats(locationGroupStatisticsList)
                .osStats(osGroupStatisticList)
                .deviceStats(deviceGroupStatisticList)
                .browserStats(browserGroupStatisticList)
                .daily(daily)
                .weekdayStats(weekDayStatistic)
                .hourStats(hourStatistic)
                .topIpStats(topIpStats)
                .uvTypeStats(userTypeGroupStatisticList)
                .build();
    }
}
