package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.encryption.util.SM3Utils;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.util.IDCardUtil;
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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @Author: caic
 * @version: 10:40 2019/1/7
 * @Description:
 */
@Data
@RestController
@RequestMapping("/sys/user")
@Slf4j
public class SysUserController {
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

    Pattern patternCn = Pattern.compile("^[\u4E00-\u9FA5]+$");
    Pattern patternEn = Pattern.compile("^[A-Za-z]+$");
    Pattern patternEnCn = Pattern.compile("^[0-9a-zA-Z]+$");

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
            if (!patternEnCn.matcher(sysUser.getLogonName()).matches()) {
                throw new AppException("登录名必须是英文或数字");
            }
            if (!patternCn.matcher(sysUser.getDisplayName()).matches() && !patternEn.matcher(sysUser.getDisplayName()).matches()) {
                throw new AppException("姓名必须是中文或英文");
            }
            if ("1".equals(sysUser.getCardType()) && !IDCardUtil.verify(sysUser.getCardId())) {
                throw new AppException("身份证号码格式不正确");
            }
            if ("1".equals(sysUser.getUserType()) && !"1".equals(currentUserService.getCurrentUser().getUserType())) {
                throw new AppException("非超级管理员不能创建超级管理员");
            }
            String sm3LogonName = SM3Utils.digest(sysUser.getLogonName());
            String sm3ReverseLogonName = SM3Utils.digest(new StringBuffer(sysUser.getLogonName()).reverse().toString());
            if (sm3LogonName.equals(sysUser.getPassWD()) || sm3ReverseLogonName.equals(sysUser.getPassWD())) {
                throw new AppException("新密码不能与登录名（包含逆序）相同！");
            }
            sysUser.setUserState("1");
            JSONArray orgIds = pageData.getJSONObject("orgId").getJSONArray("value");
            if (orgIds.size() > 0)
                sysUser.setOrgId(Long.parseLong(orgIds.get(orgIds.size() - 1) + ""));

            JSONArray aa26Ids = pageData.getJSONObject("aa26").getJSONArray("value");
            if (aa26Ids.size() > 0)
                sysUser.setAreaId(Long.parseLong(aa26Ids.get(aa26Ids.size() - 1) + ""));

            JSONArray roleIds = pageData.getJSONObject("roleIds").getJSONArray("value");
            List<SysUserRole> list = new ArrayList<>();
            for (Object roleId : roleIds) {
                if (roleId != null) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setRoleId(roleId.toString());
                    list.add(sysUserRole);
                }
            }
            sysUser.setSysUserRoleList(list);
            sysUser.setCreateTime(new Date());
            // 设定用户过期时间
            sysUser.setUserExpireDate(pageData.getJSONObject("userExpireDate").getDate("value"));
            // 获取密码过期策略
            String pwExpireType = (String) pageData.getJSONObject("pwExpireType").get("value");
            if (!ObjectUtils.isEmpty(pwExpireType) && pwExpireType.equals("1")) {
                // 系统配置周期
                long l = new Date().getTime() + 7 * 24 * 3600 * 1000;
                sysUser.setPwExpireDate(new Date(l));
            } else if (ObjectUtils.isEmpty(pwExpireType) || pwExpireType.equals("2")) {
                // 永不过期
                sysUser.setPwExpireDate(null);
            } else if (!ObjectUtils.isEmpty(pwExpireType) && pwExpireType.equals("3")) {
                // 指定日期
                sysUser.setPwExpireDate(pageData.getJSONObject("pwExpireDate").getDate("value"));

            }


            JSONArray areaIds = pageData.getJSONObject("aa26Tree").getJSONArray("value");
            List<String> addAreaIds = new ArrayList<>();
            List<String> removeAreaIds = new ArrayList<>();
            for (int i = 0; i < areaIds.size(); i++) {
                JSONObject obj = areaIds.getJSONObject(i);
                if ((boolean) obj.get("value")) {
                    addAreaIds.add(obj.get("key").toString());
                } else {
                    removeAreaIds.add(obj.get("key").toString());
                }
            }
            Map<String, List<String>> map = new ConcurrentHashMap<>();
            map.put("addAreaIds", addAreaIds);
            map.put("removeAreaIds", removeAreaIds);

            if (!validator.validateArea(sysUser.getAreaId() + "", sysUser.getUserType())
                    || !validator.validateOrg(sysUser.getOrgId(), sysUser.getUserType())
                    || !validator.validateRoleIds(roleIds.toJavaList(String.class))) {
                throw new AppException("无权操作");
            }

            sysUserService.saveUser(sysUser, map);

            return ResponseMessage.ok("保存成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseMessage.error("保存失败，失败原因：" + e.getMessage());
        }

    }

    @GetMapping("/queryOneUser/{userId}")
    public ResponseMessage queryOneUser(@PathVariable String userId) throws AppException {
        SysUser sysUser = sysUserService.queryOneUser(userId);
        if (!validator.validateUser(sysUser.getUserId())) {
            throw new AppException("无权查看");
        }
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
        Page<UserDTO> list = sysUserService.findAll(logonName, displayName, orgId, userState,userType,aa26,cardId, page, size,"1");
        JSONObject jsonObject=new JSONObject();
        JSONArray jsonArray=JSONArray.parseArray(JSONArray.toJSONString(list.getContent()));
        for(int i=0; i < jsonArray.size(); i++){
            if(jsonArray.getJSONObject(i).getString("orgId") != null) {
                SysOrg sysOrg = sysOrgService.findByOrgid(Long.valueOf(jsonArray.getJSONObject(i).getString("orgId")));
                if(sysOrg != null){
                    jsonArray.getJSONObject(i).put("orgId", sysOrg.getOrgname());
                }else{
                    jsonArray.getJSONObject(i).put("orgId", "");
                }
            }
            if(jsonArray.getJSONObject(i).getString("areaId") != null) {
                Aa26 a=aa26Service.findByAab301(jsonArray.getJSONObject(i).getString("areaId"));
                if(a != null) {
                    jsonArray.getJSONObject(i).put("areaId", a.getAaa146());
                }else{
                    jsonArray.getJSONObject(i).put("areaId", "");
                }
            }
        }
        jsonObject.put("totalElements",list.getTotalElements());
        jsonObject.put("content",jsonArray);
        return ResponseMessage.ok(jsonObject);
    }

    /**
     * 复制用户
     * @param data
     * @return
     */
    @PostMapping(value = "/copyUser")
    public ResponseMessage copyUser(@RequestBody JSONObject data){
        try {
            if (!validator.validateUser(currentUserService.getCurrentUser().getUserId())) {
                throw new AppException("无权操作");
            }
            sysUserService.copyUser(data);
            return ResponseMessage.ok("复制成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseMessage.error("复制失败原因：" + e.getMessage());
        }
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
            sysUserService.clearSessions(userId);
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
            sysUserService.clearSessions(userId);
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
//            return ResponseMessage.ok("重置密码成功,默认密码为：" + resetPassWD);
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


    /**
     * 获取当前登录用户
     * @return
     */
    @GetMapping("/getUserSlevel")
    public ResponseMessage getUserSlevel(){

        /*return ResponseMessage.ok(sysUserService.getUserSlevel());*/
        ResponseMessage t = new ResponseMessage();
        t.setStatus("0");
        t.setData(sysUserService.getUserSlevel());
        return t;
    }


}

