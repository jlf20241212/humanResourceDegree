package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.MonitorCentersDTO;
import com.insigma.sys.entity.SysMenu;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.MonitorCentersService;
import com.insigma.web.support.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author GH
 * @ClassName: MonitorCentersController
 * @Description: 监控中心
 * @version 2021/12/7  14:53
 */
@Slf4j
@RestController
@RequestMapping("/sys/monitoringcenter/MonitoringDetails")
public class MonitorCentersController extends BaseController {
    @Autowired
    MonitorCentersService monitorCentersService;
    @Autowired
    private CurrentUserService currentUserService;
    /**
     * 查询应用数据源信息
     *
     * @return
     */
    @PostMapping("/querServiceDataSouce")
    public JSONObject querServiceDataSouce(@RequestBody MonitorCentersDTO monitorCentersDTO) {
        return monitorCentersService.querServiceDataSouce(monitorCentersDTO);
    }

    /**
     * 查询应用SQL监控列表
     *
     * @return
     */
    @PostMapping("/querServiceDataSqlList")
    public JSONObject querServiceDataSqlList(@RequestBody MonitorCentersDTO MonitorCentersDTO) {
        return monitorCentersService.querServiceDataSqlList(MonitorCentersDTO);
    }

    /**
     * 查询WBEAPP监控信息
     *
     * @return
     */
    @PostMapping("/querServiceWebApp")
    public JSONObject querServiceWebApp(@RequestBody MonitorCentersDTO monitorCentersDTO) {
        return monitorCentersService.querServiceWebApp(monitorCentersDTO);
    }

    /**
     * 查询应用URI监控列表
     *
     * @return
     */
    @PostMapping("/querServiceDataUriList")
    public JSONObject querServiceDataUriList(@RequestBody MonitorCentersDTO monitorCentersDTO) {
        return monitorCentersService.querServiceDataUriList(monitorCentersDTO);
    }

    /**
     * 查询应用Session监控列表
     *
     * @return
     */
    @PostMapping("/querServiceDataSessionList")
    public JSONObject querServiceDataSessionList(@RequestBody MonitorCentersDTO monitorCentersDTO) {
        return monitorCentersService.querServiceDataSessionList(monitorCentersDTO);

    }

    /**
     * 查询每个应用下对应的实例IP
     *
     * @return
     */
    @PostMapping("/querServiceIp")
    public JSONObject querServiceIp(@RequestBody MonitorCentersDTO monitorCentersDTO) {
        return monitorCentersService.querServiceIp(monitorCentersDTO);
    }

    //获取菜单名称和id实体
    @PostMapping("/queryEntity")
    public ResponseMessage queryEntity(@RequestBody JSONObject jsonObject) {
        String url = jsonObject.getString("url");
        SysMenu sysMenu = monitorCentersService.queryEntity(url);
        return ResponseMessage.ok(sysMenu);
    }

    /**
     * 清理全部监控数据
     *
     * @return
     */
    @PostMapping("/clearAllMonitorData")
    public JSONObject clearAllMonitorData() {
        return monitorCentersService.clearAllMonitorData();
    }

    /**
     * 清理对应的监控数据
     *
     * @return
     */
    @PostMapping("/clearOneServiceData")
    public JSONObject clearOneServiceData(@RequestBody MonitorCentersDTO monitorCentersDTO) {
        return monitorCentersService.clearOneServiceData(monitorCentersDTO);
    }



    //查询用户是否为超级管理员
    @GetMapping("/queryUser")
    public ResponseMessage queryUser() {
        SysUser sysUser = currentUserService.getCurrentUser();
        //超级管理员
        if ("1".equals(sysUser.getUserType())) {
            return ResponseMessage.ok(true);
        } else {
            return ResponseMessage.ok(false);
        }
    }
}


