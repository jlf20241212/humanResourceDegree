package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.encryption.util.SM3Utils;
import com.insigma.framework.util.StringUtil;
import com.insigma.framework.web.securities.commons.SM3PasswordEncoder;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.SysAppAddressDTO;
import com.insigma.sys.dto.SysAppDTO;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.SysAppService;
import com.insigma.web.support.annotation.OdinRequest;
import com.insigma.web.support.annotation.OdinRequestParam;
import com.insigma.web.support.controller.BaseController;
import com.insigma.web.support.service.token.AppTokenService;
import com.insigma.web.support.util.pe.PageTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;

/**
 * 应用系统管理接口
 *
 * @author GH
 * @version 2022/3/28
 * @since 2.6.5
 */
@Slf4j
@RestController
@RequestMapping("/sys/sysapp/sysApp")
public class SysAppController extends BaseController {

    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private SysAppService sysAppService;
    @Autowired
    private AppTokenService appTokenService;


    @OdinRequest(init = true)
    @PostMapping("/doInit")
    public ResponseMessage doInit(@OdinRequestParam("f_form") SysAppDTO queryDTO, Integer size) throws SQLException {
        tableDataQuery(queryDTO, 1, size);
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/query")
    public ResponseMessage doGridQuery(@OdinRequestParam("f_form") SysAppDTO queryDTO,
                                       Integer size) {
        tableDataQuery(queryDTO, 1, size);
        return this.ok();
    }

    @OdinRequest
    @PostMapping("/doGridQuery/{name}")
    public ResponseMessage doGridQuery(@OdinRequestParam("f_form") SysAppDTO queryDTO,
                                       @OdinRequestParam("t_tableData_page") Integer page,
                                       @OdinRequestParam("t_tableData_size") Integer size,
                                       @PathVariable String name) {
        tableDataQuery(queryDTO, page, size);
        this.set("page", page);
        return this.ok();
    }

    public void tableDataQuery(SysAppDTO queryDTO, Integer page, Integer size) {
        this.pageQuery("t_tableData", () -> {
            this.set("page", page); // 需要将前端表格中:currentPage定义的值设置成page的值
            return sysAppService.pageQuery(queryDTO, page, size);
        });
    }


    @OdinRequest
    @PostMapping("/add")
    public ResponseMessage add() {
        this.set("dialogFormVisible", true);
        this.set("dialogFormTitle", "新增应用");
        this.clearForm("f_sysAppForm");
        this.getTable("t_tableData1").setData(new ArrayList<>());
        this.getTable("t_tableData1").setTotal(0L);
        return this.ok();
    }


    @OdinRequest
    @PostMapping("/doSave/{name}")
    public ResponseMessage doSave(@OdinRequestParam("f_sysAppForm") SysAppDTO queryDTO) {
        PageTable t_tableData1 = this.getTable("t_tableData1");
        List<SysAppAddressDTO> listsysAppAddressDTO = t_tableData1.getData(SysAppAddressDTO.class);
        String portalurl = null;
        String appurl = null;
        for (int i = 0; i < listsysAppAddressDTO.size(); i++) {
            portalurl = listsysAppAddressDTO.get(i).getPortalUrl();
            if (portalurl == null || "".equals(portalurl)) {
                return this.error("门户名称不能为空");
            }
            appurl = listsysAppAddressDTO.get(i).getAppUrl();
            if (appurl == null || "".equals(appurl)) {
                return this.error("应用地址不能为空");
            } else {
                if (appurl.indexOf("http://") == 0 || appurl.indexOf("https://") == 0) {

                } else {
                    return this.error("门户名称为" + portalurl + "的应用地址不合法，请检查！");
                }
            }
        }
        if (ObjectUtils.isEmpty(queryDTO.getAppId())) {
            boolean flag = sysAppService.selectAppCode(queryDTO.getAppCode());
            if (flag) {
                return ResponseMessage.error("异常应用编码重复！");
            }
        }
        String secretKey = null;
        if (StringUtil.isEmpty(queryDTO.getAppId())) {
            secretKey = appTokenService.generateSecretKey();
        }
        String appId = sysAppService.saveSysAppDTO(queryDTO, secretKey);
        if ("".equals(appId) && appId == null) {
            return ResponseMessage.error("保存失败！");
        }else {
            sysAppService.saveSysAppAddressDTO(listsysAppAddressDTO, appId);
        }
        this.set("dialogFormVisible", false);
        this.clearForm("f_sysAppForm");
        this.getTable("t_tableData1").setData(new ArrayList<>());
        this.getTable("t_tableData1").setTotal(0L);
        this.refresh();
        return this.ok("保存成功！");
    }

    @OdinRequest
    @PostMapping("/update")
    public ResponseMessage update(String appId) {
        SysAppDTO sysAppDTO = sysAppService.querySysApp(appId);
        this.toForm("f_sysAppForm", sysAppDTO);
        List<SysAppAddressDTO> list = sysAppService.querySysAddressDTO(appId);
        this.getTable("t_tableData1").setData(list);
        this.set("dialogFormVisible", true);
        this.set("dialogFormTitle", "修改应用");
        return this.ok();
    }

    @OdinRequest(refresh = true) // refresh = true 请求结束后刷新页面
    @PostMapping("/delete")
    public ResponseMessage delete(String appId) {
        sysAppService.deleteSysApp(appId);
        sysAppService.deleteSysAppAddressByAppId(appId);
        return this.ok("删除成功！");
    }

    @PostMapping("/deleteSysAppAddress")
    public ResponseMessage deleteSysAppAddress(@RequestBody SysAppAddressDTO sysAppAddressDTO) {
        if (sysAppAddressDTO.getAddressId() == null || "".equals(sysAppAddressDTO.getAddressId())) {
            return this.ok("删除成功！");
        }
        sysAppService.deleteSysAppAddress(sysAppAddressDTO.getAddressId());
        return this.ok("删除成功！");
    }

    @GetMapping("/toActiveSysApp")
    public ResponseMessage toActiveSysApp(@RequestParam(name = "appId") String appId) {
        sysAppService.toActiveSysApp(appId);
        return this.ok("启用成功！");
    }

    @GetMapping("/toNotActiveSysApp")
    public ResponseMessage toNotActiveSysApp(@RequestParam(name = "appId") String appId) {
        sysAppService.toNotActiveSysApp(appId);
        return this.ok("禁用成功！");
    }

    @PostMapping("/verify")
    public ResponseMessage verify(@RequestBody JSONObject jsonObject) {
        //获取当前登录人去验证密码是否正确
        SysUser sysUser = currentUserService.getCurrentUser();
        String password = jsonObject.getString("password");
        String id = jsonObject.getString("id");
        SM3PasswordEncoder sm3PasswordEncoder = new SM3PasswordEncoder();
        boolean b = sm3PasswordEncoder.matches(SM3Utils.digest(password), sysUser.getPassWD());
        HashMap<String, Object> map = new HashMap<>();
        if(b){
            //查询安全key
            SysAppDTO sysAppDTO = sysAppService.querySysApp(id);
            map.put("appId",sysAppDTO.getAppId());
            map.put("secretKey",sysAppDTO.getSecretKey());
            map.put("verify",b);
        }
        map.put("verify",b);
        return ResponseMessage.ok(map);
    }

}
