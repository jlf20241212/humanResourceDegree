package com.insigma.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.PageResponseMessage;
import com.insigma.framework.ResponseMessage;
import com.insigma.framework.commons.syslog.Syslog;
import com.insigma.framework.db.PageInfo;
import com.insigma.framework.util.TreeUtil;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.common.SysManageMode;
import com.insigma.sys.dto.MenuDTO;
import com.insigma.sys.dto.SysRoleDTO;
import com.insigma.sys.dto.UserDTO;
import com.insigma.sys.entity.SysRole;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.MenuService;
import com.insigma.sys.service.RoleService;
import com.insigma.sys.service.SysCodeService;
import com.insigma.web.support.service.CodeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxy on 2019/1/4.
 */
@RestController
@RequestMapping("/sys/role")
public class RoleController {
    @Autowired
    private CodeTypeService codeService;
    @Autowired
    private SysCodeService sysCodeService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private CurrentUserService currentUserService;

    /**
     * 初始化页面数据
     *
     * @return
     */
    @PostMapping("/doInit")
    public ResponseMessage initPageDate(@RequestBody JSONObject jsonObject) {
        JSONObject codeTypes = jsonObject.getJSONObject("codeTypes");
        JSONObject codeTypesRes = codeService.getCodeTypes(codeTypes);
        jsonObject.put("codeTypes", codeTypesRes);
        return ResponseMessage.ok(jsonObject);

    }


    @PostMapping("/initCodeTypes")
    public ResponseMessage initCodeTypes(@RequestBody JSONObject jsonObject) {
        ResponseMessage rm;
        try {
            JSONObject codeTypes = roleService.getCodeTypes(jsonObject);
            rm = ResponseMessage.ok(codeTypes);
        } catch (Exception e) {
            rm = ResponseMessage.error(e.getMessage());
        }
        return rm;

    }


    @Syslog("${sysRole.rolename}")
    @PostMapping("/save")
    public ResponseMessage saveRole(@RequestBody SysRole sysRole) {
        PageResponseMessage rm = null;
        String roleid = sysRole.getRoleid();
        String rolename = sysRole.getRolename();
        boolean flag = false; //true代表有重复
        /*if(!"".equals(roleid) && roleid!=null){
            //flag=false;
            flag=roleService.checkRoleByRolename(rolename);
            if(flag){
                flag=false;
            }else{
                rm = ResponseMessage.error("该角色名称已被其他行政区或机构占用！");
                return rm;
            }
        }else{
            flag=roleService.checkRoleByRolename(rolename);
        }*/

        flag = roleService.checkRoleByRolename(rolename);
        if (!"".equals(roleid) && roleid != null) {//说明是更新
            flag = roleService.checkRoleByRolenameAndRoleid(rolename, roleid);
        }
        if (flag) {
            try {
                /*List<SysUser> listUser= currentUserService.getCurrentUserByLoginname("admin");
                long areaid=listUser.get(0).getAreaId();
                long orgid=listUser.get(0).getOrgId();
                String userid=listUser.get(0).getUserId();*/
                SysUser sysUser = currentUserService.getCurrentUser();
               /* long areaid = listUser.get(0).getAreaId();
                long orgid = listUser.get(0).getOrgId();
                String userid = listUser.get(0).getUserId();
                String usertype = listUser.get(0).getUserType();*/
                Long areaid = sysUser.getAreaId();
                Long orgid = sysUser.getOrgId();
                String userid = sysUser.getUserId();
                String usertype = sysUser.getUserType();
                sysRole.setAreaid(areaid);
                sysRole.setOrgid(orgid);
                sysRole.setCreatorid(userid);
                roleService.save(sysRole);
                rm = PageResponseMessage.ok("保存成功");
            } catch (Exception e) {
                rm = PageResponseMessage.error(e.getMessage());
            }
        } else {
            rm = PageResponseMessage.error("该角色名称已被其他行政区或机构占用！");
        }
        return rm;
    }


   /* @PostMapping("/queryRole")
    public ResponseMessage queryRole(@RequestBody JSONObject pageData){
        ResponseMessage rm;
        try {
            List<SysRole> sysRoles=roleService.queryRole(pageData);
            rm=ResponseMessage.ok("",sysRoles);
        }catch (Exception e){
            rm = ResponseMessage.error(e.getMessage());
        }
        return rm;
    }*/

    /*@PostMapping("/queryRole")
    public ResponseMessage queryRole(@RequestParam(name="rolename")String rolename,@RequestParam(name="roledesc")
    String roledesc,
                                     @RequestParam(name="roletype")String roletype,@RequestParam(name="page")Integer
                                     page,
                                     @RequestParam(name="size") Integer size){
        ResponseMessage rm;
        try {
            SysRoleDTO sysRoleDTO=roleService.query(rolename, roledesc, roletype, page, size);
            rm=ResponseMessage.ok("",sysRoleDTO);
        }catch (Exception e){
            rm = ResponseMessage.error(e.getMessage());
        }
        return rm;
    }*/

    @GetMapping("/queryRole")
    public ResponseMessage queryRole(@RequestParam(name = "rolename") String rolename, @RequestParam(name = "roledesc"
    ) String roledesc,
                                     @RequestParam(name = "roletype") String roletype,
                                     @RequestParam(name = "page") Integer page,
                                     @RequestParam(name = "size") Integer size) {
        ResponseMessage rm;
        //查询当前登录用户信息
        //List<SysUser> listUser = currentUserService.getCurrentUserByLoginname("admin");
        SysUser sysUser = currentUserService.getCurrentUser();
       /* long areaid = listUser.get(0).getAreaId();
        long orgid = listUser.get(0).getOrgId();
        String userid = listUser.get(0).getUserId();
        String usertype = listUser.get(0).getUserType();*/
        Long areaid = sysUser.getAreaId();
        Long orgid = sysUser.getOrgId();
        String userid = sysUser.getUserId();
        String usertype = sysUser.getUserType();

        if (SysManageMode.isTripleMode()) {//三员制
            if ("5".equals(usertype)) {//安全管理员
                usertype = "1";//按照超级管理员处理，可以查询所有的角色
            }
        }
        SysRoleDTO sysRoleDTO = null;
        if (!"".equals(usertype) && usertype != null) {
            //查询当前用户拥有的角色
            //List<SysRole> listRole=roleService.queryRoleByUserId(userid);
            try {
                sysRoleDTO = roleService.query(rolename, roledesc, roletype, page, size, areaid, orgid, usertype);
                rm = ResponseMessage.ok("", sysRoleDTO);
            } catch (Exception e) {
                rm = ResponseMessage.error(e.getMessage());
            }
        } else {//非管理员角色，不能看到任何角色
            rm = ResponseMessage.ok();
        }

        return rm;
    }

    @PostMapping("/delete")
    public ResponseMessage delete(@RequestBody SysRole sysRole) {
        SysRole role = roleService.getRoleById(sysRole.getRoleid());
        if (role == null) {
            return ResponseMessage.error("角色不存在");
        }
        SysUser sysUser = currentUserService.getCurrentUser();
        if (!roleService.roleCanOperate(sysUser, role)) {
            return ResponseMessage.error("无权操作");
        }
        roleService.deleteRoleRef(sysRole);
        return ResponseMessage.ok("删除成功");
    }

    @GetMapping("/queryTree")
    public ResponseMessage queryTable(String roletype) {
        String funtype = "";
        if (roletype != null && !"".equals(roletype)) {
            if ("1".equals(roletype)) {//管理员角色
                funtype = "1";
            } else if ("2".equals(roletype)) {
                funtype = "2";
            } else if ("3".equals(roletype)) {
                funtype = "3";
            } else if ("4".equals(roletype)) {
                funtype = "4";
            }
        }
        List<MenuDTO> menuDTOS = menuService.queryMenuByFuntypeAndActive(funtype);
        /*SysUser sysUser = currentUserService.getCurrentUser();
        List<MenuDTO> menuDTOS = new ArrayList<>();
        if ("1".equals(sysUser.getUserType())) {
            // 超级管理员，查询所有
            menuDTOS = menuService.queryMenuByFuntypeAndActive(funtype);
        } else if ("2".equals(sysUser.getUserType()) || "3".equals(sysUser.getUserType())) {
            // 行政区管理员和机构管理员查询自己拥有的角色
            menuDTOS = menuService.queryMenusByUserId(sysUser.getUserId());
        } else {
            // 其他无权限查询
            throw new AppException("无权查询！");
        }*/
        JSONArray jsonArray = TreeUtil.listToTree(JSONArray.parseArray(JSONArray.toJSONString(menuDTOS)), "functionid"
                , "parentid", "children", "0");
        return ResponseMessage.ok("查询成功", jsonArray);
    }

    @GetMapping("/getCheckedTree")
    public ResponseMessage getCheckedTree(String roleId) {
        List<Long> list = roleService.queryFuncitonidByRoleid(roleId);
        return ResponseMessage.ok("查询成功", list);
    }

    @PostMapping("/addRoleRef")
    public ResponseMessage addRoleRef(@RequestBody JSONObject jsonObject) {
        String roleid = (String) jsonObject.get("roleId");
        SysRole role = roleService.getRoleById(roleid);
        if (role == null) {
            return ResponseMessage.error("角色不存在");
        }
        SysUser sysUser = currentUserService.getCurrentUser();
        if (!roleService.roleCanOperate(sysUser, role)) {
            return ResponseMessage.error("无权操作");
        }
        roleService.saveGrant(currentUserService.getCurrentUser(), role, jsonObject);
        return ResponseMessage.ok("授权成功");
    }


    @GetMapping("getRoleTypeCode")
    public ResponseMessage getUserTypeCode() {
        List<Map<String, Object>> list = roleService.findRoleTypesCode();
        return ResponseMessage.ok(list);
    }

    @GetMapping("/queryToGrantUser")
    public ResponseMessage queryToGrantUser(
            @RequestParam(name = "roleId") String roleId,
            @RequestParam(name = "roleType") String roleType,
            @RequestParam(name = "logonname") String logonname,
            @RequestParam(name = "displayname") String displayname,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size") Integer size) throws SQLException {
        SysUser sysUser = currentUserService.getCurrentUser();
        PageInfo<UserDTO> list = roleService.queryToGrantUser(roleId, roleType, logonname, displayname, page, size);
        return ResponseMessage.ok("查询成功", list);
    }

    @GetMapping("/queryGrantedUser")
    public ResponseMessage queryGrantedUser(@RequestParam(name = "roleId") String roleId) throws SQLException {
        SysUser sysUser = currentUserService.getCurrentUser();
        SysRole sysRole = roleService.getRoleById(roleId);
        if (!roleService.roleCanOperate(sysUser, sysRole)) {
            return ResponseMessage.error("无权访问");
        }
        List<UserDTO> list = roleService.queryGrantedUser(roleId);
        return ResponseMessage.ok("查询成功", list);
    }

    @GetMapping("/removeGrantUserRole")
    public ResponseMessage removeGrantUserRole(@RequestParam(name = "roleId") String roleId,
                                               @RequestParam(name = "userId") String userId) throws SQLException {
        roleService.removeGrantUserRole(roleId, userId);
        return ResponseMessage.ok();
    }

    @PostMapping("/saveGrantUser")
    public ResponseMessage saveGrantUser(@RequestBody JSONObject jsonObject) throws SQLException {
        roleService.saveGrantUser(jsonObject);
        return ResponseMessage.ok("保存成功");
    }

}
