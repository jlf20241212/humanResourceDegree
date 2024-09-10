package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.util.TreeUtil;
import com.insigma.sys.common.CommonValidator;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.dto.UserDTO;
import com.insigma.sys.entity.*;
import com.insigma.sys.service.Aa26Service;
import com.insigma.sys.service.SysOrgService;
import com.insigma.sys.service.SysUserService;
import com.insigma.web.support.service.CodeTypeService;
import com.insigma.web.support.util.JavaBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/sys/grant")
@Slf4j
public class SysGrantController {
    @Autowired
    private CodeTypeService codeTypeService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private Aa26Service aa26Service;

    @Autowired
    private CommonValidator validator;

    /**
     * 初始化页面数据
     *
     * @return
     */
    @PostMapping("/doInit")
    public ResponseMessage initPageDate(@RequestBody JSONObject jsonObject) {
        String userType = currentUserService.getCurrentUser().getUserType();//获取当前登录用户类型
        JSONObject codeTypes = jsonObject.getJSONObject("codeTypes");
        JSONObject codeTypesRes = codeTypeService.getCodeTypes(codeTypes);
        jsonObject.put("codeTypes", codeTypesRes);
        jsonObject.put("currentUserType", userType);//设置当前登录用户类型
        return ResponseMessage.ok(jsonObject);
    }

    /**
     * 保存用户
     *
     * @param pageData
     * @return
     */
    @PostMapping("/doSave")
    public ResponseMessage save(@RequestBody JSONObject pageData) {
        try {
            SysUser sysUser = JavaBeanUtils.pageElementToBean(pageData,SysUser.class);
            JSONArray roleIds = pageData.getJSONObject("roleIds").getJSONArray("value");
            List<SysUserRole> list = new ArrayList<>();
            for (Object roleId : roleIds) {
                if (roleId != null) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setRoleId(roleId.toString());
                    list.add(sysUserRole);
                }
            }
            if (!validator.validateRoleIds(roleIds.toJavaList(String.class))) {
                throw new AppException("无权操作");
            }
            sysUser.setSysUserRoleList(list);
            sysUserService.saveUserRole(sysUser);
            ResponseMessage rm = ResponseMessage.ok("保存成功");

            return rm;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseMessage.error("保存失败，失败原因：" + e.getMessage());
        }

    }

    @GetMapping("/queryOneUser/{userId}")
    public ResponseMessage queryOneUser(@PathVariable String userId) throws AppException {
        SysUser sysUser = sysUserService.queryOneUser(userId);
        //sysUser.setPassWD(null);
        List<SysUserRole> roleList = sysUserService.queryUserRole(userId);
        List<SysUserArea> areaList = sysUserService.queryUserArea(userId);
        SysOrg sysOrg = sysOrgService.findByOrgid(sysUser.getOrgId());
        Aa26 aa26 = aa26Service.findByAab301(sysUser.getAreaId() + "");
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("sysUser", sysUser);
        map.put("roleList", roleList);
        map.put("areaList", areaList);

        if (sysOrg != null) {
            map.put("orgId", sysUser.getOrgId());
            map.put("sysOrgs",sysOrg);
            //sysOrgService.findByRegioncode(sysUser.getAreaId()+"")
        }
        if (aa26 != null)
            map.put("aa26", aa26);
        return ResponseMessage.ok(map);
    }

    @GetMapping("/queryTable")
    public ResponseMessage queryTable(@RequestParam(name = "logonName") String logonName,
                                      @RequestParam(name = "displayName") String displayName,
                                      @RequestParam(name = "orgId") String orgId,
                                      @RequestParam(name = "userState") String userState,
                                      @RequestParam(name = "userType") String userType,
                                      @RequestParam(name = "aa26") String aa26,
                                      @RequestParam(name = "cardId") String cardId,
                                      @RequestParam(name = "page") Integer page,
                                      @RequestParam(name = "size") Integer size) {
        Page<UserDTO> list = sysUserService.findAll(logonName, displayName, orgId, userState,userType,aa26,cardId, page, size,"3");
        return ResponseMessage.ok(list);
    }

    /**
     * 注销用户
     *
     * @param data
     * @return
     */
    @PostMapping(value = "/logoutUser")
    public ResponseMessage logoutUser(@RequestBody(required = true) JSONObject data) {
        try {
            String userId = data.getString("userId");
            if (!validator.validateUser(userId)) {
                throw new AppException("无权操作");
            }
            sysUserService.logoutUser(userId);
            return ResponseMessage.ok("注销成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseMessage.error("注销失败，失败原因：" + e.getMessage());
        }
    }

    /**
     * 解锁用户
     *
     * @param data
     * @return
     */
    @PostMapping(value = "/unlockUser")
    public ResponseMessage unlockUser(@RequestBody(required = true) JSONObject data) {
        try {
            String userId = data.getString("userId");
            if (!validator.validateUser(userId)) {
                throw new AppException("无权操作");
            }
            sysUserService.unlockUser(userId);
            return ResponseMessage.ok("解锁成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseMessage.error("解锁失败，失败原因：" + e.getMessage());
        }
    }

    /**
     * 锁定用户
     *
     * @param data
     * @return
     */
    @PostMapping(value = "/lockUser")
    public ResponseMessage lockUser(@RequestBody(required = true) JSONObject data) {
        try {
            String userId = data.getString("userId");
            if (!validator.validateUser(userId)) {
                throw new AppException("无权操作");
            }
            sysUserService.lockUser(userId);
            return ResponseMessage.ok("锁定成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseMessage.error("锁定失败，失败原因：" + e.getMessage());
        }
    }

    /**
     * 重置密码
     *
     * @param data
     * @return
     */
    @PostMapping("/resetPassWD")
    public ResponseMessage resetPassWD(@RequestBody(required = true) JSONObject data) {
        try {
            String userId = data.getString("userId");
            if (!validator.validateUser(userId)) {
                throw new AppException("无权操作");
            }
            sysUserService.resetPassWD(userId);
            return ResponseMessage.ok("重置密码成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseMessage.error("重置密码失败，失败原因：" + e.getMessage());
        }
    }

    @PostMapping("/editPass")
    public ResponseMessage editPassWD(@RequestBody(required = true) JSONObject data){
        try {
            String oldPass=data.getString("oldPass");
            String newPass=data.getString("newPass");
            sysUserService.updataPassWD(oldPass,newPass);
            return ResponseMessage.ok("修改密码成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseMessage.error("修改密码失败，失败原因：" + e.getMessage());
        }
    }

    @GetMapping("/getOrgTreeNodes/{areaId}")
    public ResponseMessage getOrgTreeNodes(@PathVariable String areaId) {
        JSONArray orgNodes = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(sysUserService.queryOrgNodes(areaId))), "orgid", "parentid", "children");
        return ResponseMessage.ok(orgNodes);
    }

    @GetMapping("/getAa26TreeNodes/{userType}")
    public ResponseMessage getAa26TreeNodes(@PathVariable String userType) {
        JSONArray orgNodes = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(sysUserService.queryAa26Nodes())), "aab301", "aaa148", "children");
        //orgNodes= JSONArray.parseArray(JSONArray.toJSONString(orgNodes).replace("aab301","id"));
        return ResponseMessage.ok(orgNodes);
    }
    @GetMapping("/getOrgTreeNodes1/{userType}")
    public ResponseMessage getOrgTreeNodes1(@PathVariable String userType) {
        JSONArray orgNodes = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(sysUserService.findAllOrg())), "orgid", "parentid", "children");
        //orgNodes= JSONArray.parseArray(JSONArray.toJSONString(orgNodes).replace("aab301","id"));
        return ResponseMessage.ok(orgNodes);
    }
    @GetMapping("getUserTypeCode")
    public ResponseMessage getUserTypeCode() {
        List<Map<String, Object>> list = sysUserService.findUsetTypeCode();
        return ResponseMessage.ok(list);
    }

    @GetMapping("getSlevel")
    public ResponseMessage getSlevel() {
        List<Map<String, Object>> list = sysUserService.findSlevel();
        return ResponseMessage.ok(list);
    }

    @GetMapping("getUserStateCode")
    public ResponseMessage getUserStateCode() {
        return ResponseMessage.ok(sysUserService.getUserStateCode());
    }

    @GetMapping("/getRoleList/{userType}")
    public ResponseMessage getRoleList(@PathVariable("userType") String userType) {
        List<SysRole> list = sysUserService.findByRoleType(userType);
        return ResponseMessage.ok(list);
    }

    @PostMapping("/doCheck/{id}")
    public ResponseMessage doCheck(@PathVariable("id") String id, @RequestBody JSONObject data) {
        boolean bool = true;//校验是否通过
        if ("logonName".equals(id)) {
            String logonName = data.getJSONObject("logonName").getString("value");
            String userId=data.getJSONObject("userId").getString("value");
            bool = sysUserService.checkLogonName(logonName,userId);
        }
        if (bool) {
            ResponseMessage rm = ResponseMessage.error("存在相同登录名");
            return rm;
        } else {
            return ResponseMessage.ok();
        }
    }

    /**
     * 获取所有机构
     * @return
     */
    @GetMapping("/findAllOrg")
    private ResponseMessage findAllOrg() {
        List<SysOrg> list = sysUserService.findAllOrg();
        return ResponseMessage.ok(list);
    }

    /**
     * 获取所有区域
     * @return
     */
    @GetMapping("/findAllAa26")
    private ResponseMessage findAllAa26() {
        List<Aa26> list = sysOrgService.getAA26();
        return ResponseMessage.ok(list);
    }

    /**
     * 获取所有机构,返回格式为树结构
     * @return
     */
    @GetMapping("/findAllOrgTree")
    private ResponseMessage findAllOrgTree() {
        List<SysOrg> list = sysUserService.findAllOrg();
        JSONArray orgNodes = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(list)), "orgid", "parentid", "children");

        return ResponseMessage.ok(orgNodes);
    }

    /**
     * 通用获取下拉框选项
     * @param codeType
     * @return
     */
    @GetMapping("/getAa10Code/{codeType}")
    public ResponseMessage getAa10Code(@PathVariable String codeType){
        List<Map<String, Object>> list = sysUserService.getAa10Code(codeType);
        return ResponseMessage.ok(list);
    }

    /**
     * 获取当前登录用户
     * @return
     */
    @GetMapping("/getCurrentUser")
    public ResponseMessage getCurrentUser(){
        return ResponseMessage.ok(currentUserService.getCurrentUser());
    }

}

