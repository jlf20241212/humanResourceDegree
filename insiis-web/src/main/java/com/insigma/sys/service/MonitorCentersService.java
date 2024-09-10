package com.insigma.sys.service;

import com.alibaba.fastjson.JSONObject;
import com.insigma.sys.dto.MonitorCentersDTO;
import com.insigma.sys.entity.SysMenu;

import java.util.List;
import java.util.Map;

/**
 * @author GH
 * @ClassName: MonitorCentersService
 * @Description:
 * @version 2021/12/7  15:28
 */
public interface MonitorCentersService {

    /**
     * 查询应用数据源信息
     *
     * @return
     */
    JSONObject querServiceDataSouce(MonitorCentersDTO monitorCentersDTO);

    /**
     * 查询应用SQL监控列表
     *
     * @return
     */
    JSONObject querServiceDataSqlList(MonitorCentersDTO monitorCentersDTO);

    /**
     * 查询WBEAPP监控信息
     *
     * @return
     */
    JSONObject querServiceWebApp(MonitorCentersDTO monitorCentersDTO);

    /**
     * 查询应用URI监控列表
     *
     * @return
     */
    JSONObject querServiceDataUriList(MonitorCentersDTO monitorCentersDTO);

    /**
     * 查询应用Session监控列表
     *
     * @return
     */
    JSONObject querServiceDataSessionList(MonitorCentersDTO monitorCentersDTO);

    /**
     * 查询每个应用下对应的实例IP
     *
     * @return
     */
    JSONObject querServiceIp(MonitorCentersDTO monitorCentersDTO);

    SysMenu queryEntity(String url);

    /**
     * 清理全部监控数据
     *
     * @return
     */
    JSONObject clearAllMonitorData();

    /**
     * 清理对应的监控数据
     *
     * @return
     */
    JSONObject clearOneServiceData(MonitorCentersDTO monitorCentersDTO);

    /**
     * 获取报告数据
     *
     * @return
     */
    JSONObject getReportData(MonitorCentersDTO monitorCentersDTO);

    /**
     * 获取报告详细数据
     *
     * @return
     */
    Map<String, Object> getReportDetailedData(MonitorCentersDTO monitorCentersDTO);

    /**
     * 下载历史数据
     *
     * @return
     */
    List<Map> getDataExport(String appName);

}
