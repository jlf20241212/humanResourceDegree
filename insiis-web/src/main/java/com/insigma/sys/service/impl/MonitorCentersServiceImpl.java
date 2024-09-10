package com.insigma.sys.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.insigma.sys.dto.MonitorCentersDTO;
import com.insigma.sys.entity.SysMenu;
import com.insigma.sys.repository.MenuRepository;
import com.insigma.sys.service.MonitorCentersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author GH
 * @ClassName: MonitorCentersServiceImpl
 * @Description: 监控中心
 * @version 2021/12/7  15:28
 */
@Slf4j
@Service("MonitorCentersService")
public class MonitorCentersServiceImpl implements MonitorCentersService {

    @Value("${sys.monitor.service.url:}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MenuRepository menuRepository;


    /**
     * 查询应用数据源信息
     *
     * @return
     */
    @Override
    public JSONObject querServiceDataSouce(MonitorCentersDTO monitorCentersDTO) {
        String mon = monitorCentersDTO.getTimeStart() == null ? "" : monitorCentersDTO.getTimeStart();
        String nowDate = monitorCentersDTO.getTimeEnd() == null ? "" : monitorCentersDTO.getTimeEnd();
        if (ObjectUtils.isEmpty(monitorCentersDTO.getIp()) || "".equals(monitorCentersDTO.getIp())) {
            String getUrl = url + "/monitor/count/datasources/" + monitorCentersDTO.getAppName().toLowerCase() + "?" + "startTime=" + mon + "&endTime=" + nowDate;
            log.info("查询应用数据源信息 url是=====" + getUrl);
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        } else {
            String getUrl = url + "/monitor/count/datasources/" + monitorCentersDTO.getAppName().toLowerCase() + "/" + monitorCentersDTO.getIp() + "?" + "startTime=" + mon + "&endTime=" + nowDate;
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        }
    }

    /**
     * 查询应用SQL监控列表
     *
     * @return
     */
    @Override
    public JSONObject querServiceDataSqlList(MonitorCentersDTO monitorCentersDTO) {
        String mon = monitorCentersDTO.getTimeStart() == null ? "" : monitorCentersDTO.getTimeStart();
        String nowDate = monitorCentersDTO.getTimeEnd() == null ? "" : monitorCentersDTO.getTimeEnd();
        if (ObjectUtils.isEmpty(monitorCentersDTO.getIp()) || "".equals(monitorCentersDTO.getIp())) {
            String getUrl = url + "/monitor/count/websql/" + monitorCentersDTO.getAppName().toLowerCase() + "?" + "pageNo=" + monitorCentersDTO.getStart() + "&pageSize=" + monitorCentersDTO.getRows() + "&" + "startTime=" + mon + "&endTime=" + nowDate + "&fieldName=" + monitorCentersDTO.getSqlFieldName() + "&sort=" + monitorCentersDTO.getSort();
            log.info("查询应用SQL监控列表 url是=====" + getUrl);
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        } else {
            String getUrl = url + "/monitor/count/websql/" + monitorCentersDTO.getAppName().toLowerCase() + "/" + monitorCentersDTO.getIp() + "?" + "pageNo=" + monitorCentersDTO.getStart() + "&pageSize=" + monitorCentersDTO.getRows() + "&" + "startTime=" + mon + "&endTime=" + nowDate + "&fieldName=" + monitorCentersDTO.getSqlFieldName() + "&sort=" + monitorCentersDTO.getSort();
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        }
    }

    /**
     * 查询WBEAPP监控信息
     *
     * @return
     */
    @Override
    public JSONObject querServiceWebApp(MonitorCentersDTO monitorCentersDTO) {
        String mon = monitorCentersDTO.getTimeStart() == null ? "" : monitorCentersDTO.getTimeStart();
        String nowDate = monitorCentersDTO.getTimeEnd() == null ? "" : monitorCentersDTO.getTimeEnd();
        if (ObjectUtils.isEmpty(monitorCentersDTO.getIp()) || "".equals(monitorCentersDTO.getIp())) {
            String getUrl = url + "/monitor/count/webapp/" + monitorCentersDTO.getAppName().toLowerCase() + "?" + "startTime=" + mon + "&endTime=" + nowDate;
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        } else {
            String getUrl = url + "/monitor/count/webapp/" + monitorCentersDTO.getAppName().toLowerCase() + "/" + monitorCentersDTO.getIp() + "?" + "startTime=" + mon + "&endTime=" + nowDate;
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        }
    }

    /**
     * 查询应用URI监控列表
     *
     * @return
     */
    @Override
    public JSONObject querServiceDataUriList(MonitorCentersDTO monitorCentersDTO) {
        String mon = monitorCentersDTO.getTimeStart() == null ? "" : monitorCentersDTO.getTimeStart();
        String nowDate = monitorCentersDTO.getTimeEnd() == null ? "" : monitorCentersDTO.getTimeEnd();
        if (ObjectUtils.isEmpty(monitorCentersDTO.getIp()) || "".equals(monitorCentersDTO.getIp())) {
            String getUrl = url + "/monitor/count/weburi/" + monitorCentersDTO.getAppName().toLowerCase() + "?" + "pageNo=" + monitorCentersDTO.getStart() + "&pageSize=" + monitorCentersDTO.getRows() + "&" + "startTime=" + mon + "&endTime=" + nowDate + "&fieldName=" + monitorCentersDTO.getUriFieldName() + "&sort=" + monitorCentersDTO.getSort();
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        } else {
            String getUrl = url + "/monitor/count/weburi/" + monitorCentersDTO.getAppName().toLowerCase() + "/" + monitorCentersDTO.getIp() + "?" + "pageNo=" + monitorCentersDTO.getStart() + "&pageSize=" + monitorCentersDTO.getRows() + "&" + "startTime=" + mon + "&endTime=" + nowDate + "&fieldName=" + monitorCentersDTO.getUriFieldName() + "&sort=" + monitorCentersDTO.getSort();
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        }
    }

    /**
     * 查询应用Session监控列表
     *
     * @return
     */
    @Override
    public JSONObject querServiceDataSessionList(MonitorCentersDTO monitorCentersDTO) {
        String mon = monitorCentersDTO.getTimeStart() == null ? "" : monitorCentersDTO.getTimeStart();
        String nowDate = monitorCentersDTO.getTimeEnd() == null ? "" : monitorCentersDTO.getTimeEnd();
        if (ObjectUtils.isEmpty(monitorCentersDTO.getIp()) || "".equals(monitorCentersDTO.getIp())) {
            String getUrl = url + "/monitor/count/websession/" + monitorCentersDTO.getAppName().toLowerCase() + "?" + "pageNo=" + monitorCentersDTO.getStart() + "&pageSize=" + monitorCentersDTO.getRows() + "&" + "startTime=" + mon + "&endTime=" + nowDate + "&fieldName=" + monitorCentersDTO.getSessionFieldName() + "&sort=" + monitorCentersDTO.getSort();
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        } else {
            String getUrl = url + "/monitor/count/websession/" + monitorCentersDTO.getAppName().toLowerCase() + "/" + monitorCentersDTO.getIp() + "?" + "pageNo=" + monitorCentersDTO.getStart() + "&pageSize=" + monitorCentersDTO.getRows() + "&" + "startTime=" + mon + "&endTime=" + nowDate + "&fieldName=" + monitorCentersDTO.getSessionFieldName() + "&sort=" + monitorCentersDTO.getSort();
            JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
            return result;
        }
    }

    /**
     * 查询每个应用下对应的实例IP
     *
     * @return
     */
    @Override
    public JSONObject querServiceIp(MonitorCentersDTO monitorCentersDTO) {
        String mon = monitorCentersDTO.getTimeStart() == null ? "" : monitorCentersDTO.getTimeStart();
        String nowDate = monitorCentersDTO.getTimeEnd() == null ? "" : monitorCentersDTO.getTimeEnd();
        String getUrl = url + "/monitor/count/instances?appName=" + monitorCentersDTO.getAppName().toLowerCase() + "&" + "startTime=" + mon + "&endTime=" + nowDate;
        log.info("查询每个应用下对应的实例IP url是=====" + getUrl);
        //获取服务DB链接信息
        JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
        return result;
    }

    /**
     * 清理全部监控数据
     *
     * @return
     */
    @Override
    public JSONObject clearAllMonitorData() {
        String getUrl = url + "/monitor/datasources/clear";
        log.info("清理全部监控数据url是=====" + getUrl);
        JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
        return result;

    }

    /**
     * 清理对应的监控数据
     *
     * @return
     */
    @Override
    public JSONObject clearOneServiceData(MonitorCentersDTO monitorCentersDTO) {
        String getUrl = url + "/monitor/datasources/clear";
        log.info("清理对应的监控数据url是=====" + getUrl);
        JSONObject result = restTemplate.postForObject(getUrl, monitorCentersDTO, JSONObject.class);
        return result;

    }

    /**
     * 获取报告数据
     *
     * @return
     */
    @Override
    public JSONObject getReportData(MonitorCentersDTO monitorCentersDTO) {
        String mon = monitorCentersDTO.getTimeStart() == null ? "" : monitorCentersDTO.getTimeStart();
        String nowDate = monitorCentersDTO.getTimeEnd() == null ? "" : monitorCentersDTO.getTimeEnd();
        String getUrl = url + "/monitor/count/getReportData?appName=" + monitorCentersDTO.getAppName().toLowerCase() + "&startTime=" + mon + "&endTime=" + nowDate;
        log.info("获取报告数据url是=====" + getUrl);
        JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
        return result;
    }

    @Override
    public Map<String, Object> getReportDetailedData(MonitorCentersDTO monitorCentersDTO) {
        String mon = monitorCentersDTO.getTimeStart() == null ? "" : monitorCentersDTO.getTimeStart();
        String nowDate = monitorCentersDTO.getTimeEnd() == null ? "" : monitorCentersDTO.getTimeEnd();
        String getUrl = url + "/monitor/count/getReportDetailedData?appName=" + monitorCentersDTO.getAppName().toLowerCase() + "&startTime=" + mon + "&endTime=" + nowDate;
        log.info("获取报告详细数据url是=====" + getUrl);
        JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
        Map<String, Object> map = new HashMap<>();
        JSONObject data = result.getJSONObject("data");
        map.put("dates", data.getString("dates"));
        map.put("score", data.getString("score"));
        map.put("scoreSlowSql", data.getString("scoreSlowSql"));
        map.put("scoreErrorSql", data.getString("scoreErrorSql"));
        map.put("scoreSlowUrl",data.getString("scoreSlowUrl"));
        map.put("scoreJdbcErrorUrl", data.getString("scoreJdbcErrorUrl"));
        map.put("type", data.getString("type"));
        map.put("sqlAvgTime", data.getString("sqlAvgTime"));
        map.put("urlAvgTime", data.getString("urlAvgTime"));
        map.put("slowSql", data.getString("slowSql"));
        map.put("sqlExecuteError", data.getString("sqlExecuteError"));
        map.put("sqlExecuteErrorCount", data.getString("sqlExecuteErrorCount"));
        map.put("slowUri", data.getString("slowUri"));
        map.put("jdbcError", data.getString("jdbcError"));
        map.put("jdbcErrorCount", data.getString("jdbcErrorCount"));
        map.put("newDate", new SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss").format(new Date()));
        map.put("sqlList", data.getJSONArray("sqlList").toJavaList(Map.class));
        map.put("sqlErrorList", data.getJSONArray("sqlErrorList").toJavaList(Map.class));
        map.put("uriList", data.getJSONArray("uriList").toJavaList(Map.class));
        map.put("jdbcErrorList", data.getJSONArray("jdbcErrorList").toJavaList(Map.class));
        return map;
    }

    /**
     * 下载历史数据
     *
     * @return
     */
    @Override
    public List<Map> getDataExport(String appName) {
        String getUrl = url + "/monitor/count/getDataExport?appName=" + appName;
        JSONObject result = restTemplate.getForObject(getUrl, JSONObject.class);
        JSONObject data = result.getJSONObject("data");
        List<Map> list = data.getJSONArray("list").toJavaList(Map.class);
        return list;
    }

    /**
     * 获取菜单id
     *
     * @return
     */
    @Override
    public SysMenu queryEntity(String url) {
        SysMenu sysMenu = menuRepository.queryEntity(url);
        return sysMenu;
    }
}
