package com.insigma.sys.service;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.db.PageInfo;
import com.insigma.sys.dto.SysRoleDTO;
import com.insigma.sys.dto.UserDTO;
import com.insigma.sys.entity.SysRole;
import com.insigma.sys.entity.SysUser;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by zxy on 2019/1/7.
 */
public interface RoleService {
    void save(SysRole sysRole);

    SysRoleDTO query(String rolename, String roledesc, String roletype, Integer page, Integer size, Long areaid, Long orgid, String usertype);

    void deleteSysRole(SysRole sysRole);

    JSONObject getCodeTypes(JSONObject jsonObject);

    boolean checkRoleByRolename(String rolename);

    boolean checkRoleByRolenameAndRoleid(String rolename, String roleid);

    List<SysRole> queryRoleByUserId(String userid);

    void deleteRoleRef(SysRole sysRole);

    List<Long> queryFuncitonidByRoleid(String roleid);

    boolean checkRoleIfValidByRoleid(String roleid);

    SysRole getRoleById(String roleid);

    boolean roleCanOperate(SysUser sysUser, SysRole sysRole);

    void saveGrant(SysUser sysUser, SysRole role, JSONObject jsonObject);

    List<Map<String, Object>> findRoleTypesCode();

    PageInfo<UserDTO> queryToGrantUser(String roleId, String roletype, String logonname, String displayname,
                                       Integer page, Integer size) throws SQLException;

    List<UserDTO> queryGrantedUser(String roleId);

    void removeGrantUserRole(String roleId, String userId);

    void saveGrantUser(JSONObject jsonObject);
}
