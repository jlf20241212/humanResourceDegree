package com.insigma.sys.service;

import com.alibaba.fastjson.JSONObject;
import com.insigma.framework.exception.AppException;
import com.insigma.sys.dto.UserDTO;
import com.insigma.sys.entity.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @Author: caic
 *
 * @version: 14:49 2019/1/7
 * @Description:
 */
public interface SysUserService {
    /**
     * 删除全部用户表数据（同步）
     * @return
     */
    void deleteAll();
    /**
     * 删除用户表数据根据id（同步）
     * @return
     */
    void deleteByUserId(String id);
    /**
     * 添加用户表数据（同步）
     * @return
     */
    void saveAll(SysUser sysUser);

    /**
     * 保存和修改用户
     * @param sysUser
     * @param map
     * @throws AppException
     */
    void saveUser(SysUser sysUser, Map<String, List<String>> map) throws AppException;

    /**
     * 查询全部用户列表
     * @return
     */
    List<SysUser> queryAllUser();

    /**
     * 注销用户
     * @param userId
     * @throws AppException
     */
    SysUser logoutUser(String userId) throws AppException;

    /**
     * 解锁用户
     * @param userId
     * @throws AppException
     */
    SysUser unlockUser(String userId) throws AppException;

    /**
     * 锁定用户
     * @param userId
     * @throws AppException
     */
    SysUser lockUser(String userId) throws AppException;

    /**
     * 重置用户密码，重置密码为000000
     * @param userId
     * @throws AppException
     */
    SysUser resetPassWD(String userId) throws AppException;

    /**
     * 修改密码
     * @param oldPass
     * @param newPass
     * @throws AppException
     */
    SysUser updataPassWD(String oldPass, String newPass) throws AppException;

    /**
     * 根据区域查询区域下的机构
     * @param areaId
     * @return
     */
    List<SysOrg> queryOrgNodes(String areaId);

    /**
     * 获取全部区域
     * @return
     */
    List<Aa26> queryAa26Nodes();

    /**
     * 获取用户类型编码集合
     * @return
     */
    List<Map<String, Object>> findUsetTypeCode();

    /**
     * 分页查询用户
     * @param logonName
     * @param dsiplayName
     * @param orgId
     * @param userType
     * @param page
     * @param size
     * @return
     */
    Page<UserDTO> findAll(String logonName, String dsiplayName, String orgId, String userState, String userType, String aa26, String cardId, Integer page, Integer size,String type);

    /**
     * 获取用户状态编码集合
     * @return
     */
    List<Map<String, Object>> getUserStateCode();

    /**
     * 根据角色类型获取角色
     * @param roleType
     * @return
     */
    List<SysRole> findByRoleType(String roleType);

    /**
     * 校验用户登录名是否重复
     * @param logonName
     * @param userId
     * @return
     */
    boolean checkLogonName(String logonName, String userId);

    /**
     * 根据用户id查询单个用户
     * @param userId
     * @return
     */
    SysUser queryOneUser(String userId);

    /**
     * 单个用户修改信息
     * @param sysUser
     */
    void updateUser(SysUser sysUser);
    /**
     * 查询用户绑定的角色
     * @param userId
     * @return
     */
    List<SysUserRole> queryUserRole(String userId) throws AppException;

    /**
     * 查询用户管理的区域
     * @param userId
     * @return
     */
    List<SysUserArea> queryUserArea(String userId) throws AppException;

    /**
     * 查询全部机构
     * @return
     */
    List<SysOrg> findAllOrg();

    /**
     * 通用aa10编码集合
     * @return
     */
    List<Map<String, Object>> getAa10Code(String codeType);
    /**
     * 删除所有用户数据除去admin
     * @return
     */
    void deleteAllUserExitsAdmin();

    /**
     * 获取密级
     * @return
     */
    List<Map<String, Object>> findSlevel();

    /**
     * 保存和修改用户
     * @param sysUser
     * @throws AppException
     */
    void saveUserRole(SysUser sysUser) throws AppException;

    /**
     * 获取用户的密级等级
     * @return
     */
    public String getUserSlevel();

    void batchSaveUsers(List<SysUser> users);

    SysUser findUserByLogonName(String logonName);

    void copyUser(JSONObject data) throws AppException;

    void clearSessions(String userId);
}
