package com.insigma.sys.common;

import com.insigma.framework.exception.AppException;
import com.insigma.sys.entity.Aa26;
import com.insigma.sys.entity.SysOrg;
import com.insigma.sys.entity.SysRole;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.repository.Aa26Repository;
import com.insigma.sys.repository.RoleRepository;
import com.insigma.sys.repository.SysOrgRepository;
import com.insigma.sys.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yinjh
 * @version 2021/12/8
 */
@Component
public class CommonValidator {

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SysOrgRepository sysOrgRepository;

    @Autowired
    private Aa26Repository aa26Repository;

    @Autowired
    private SysUserRepository sysUserRepository;

    /**
     * 校验当前登录用户是否有相应角色的操作权限
     * @param roleId 角色ID
     * @return
     */
    public boolean validateRole(String roleId) {
        SysUser sysUser = currentUserService.getCurrentUser();
        SysRole sysRole = roleRepository.findById(roleId).orElseThrow(() -> new AppException("角色不存在！"));
        if ("1".equals(sysUser.getUserType())) {
            return true;
        } else if ("2".equals(sysUser.getUserType())) {
            if (sysUser.getAreaId() != null && sysUser.getAreaId().equals(sysRole.getAreaid())) {
                return true;
            }
        } else if ("3".equals(sysUser.getUserType())) {
            if (sysUser.getOrgId() != null && sysUser.getOrgId().equals(sysRole.getOrgid())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验角色列表
     * @param roleIds
     * @return
     */
    public boolean validateRoleIds(List<String> roleIds) {
        for (int i = 0; i < roleIds.size(); i++) {
            if (!validateRole(roleIds.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     *  校验当前登录用户是否有相应行政区划的操作权限
     * @param areaId 区域ID
     * @return
     */
    public boolean validateArea(String areaId, String userType) {
        SysUser sysUser = currentUserService.getCurrentUser();
        if ("1".equals(sysUser.getUserType()) && "1".equals(userType)) {
            return true;
        }
        Aa26 aa26 = aa26Repository.findById(areaId).orElseThrow(() -> new AppException("行政区划不存在！"));
        if ("1".equals(sysUser.getUserType())) {
            return true;
        } else if ("2".equals(sysUser.getUserType())) {
            List<Aa26> list = aa26Repository.findByAab301AndIdPath(sysUser.getAreaId() + "", aa26.getIdpath());
            if (list.size() > 0) {
                return true;
            }
        } else if ("3".equals(sysUser.getUserType())) {
            if (aa26.getAab301().equals(sysUser.getAreaId() + "")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验当前登录用户是否有相应机构的操作权限
     * @param orgId 机构ID
     * @return
     */
    public boolean validateOrg(Long orgId, String userType) {
        SysUser sysUser = currentUserService.getCurrentUser();
        if ("1".equals(userType)) {
            return "1".equals(sysUser.getUserType());
        }
        SysOrg sysOrg = sysOrgRepository.findById(orgId).orElseThrow(() -> new AppException("机构不存在！"));
        if ("1".equals(sysUser.getUserType())) {
            return true;
        } else if ("2".equals(sysUser.getUserType())) {
            List<SysOrg> list = sysOrgRepository.findByRegioncodeAndIdPath(sysUser.getAreaId() + "", sysOrg.getIdpath());
            if (list.size() > 0) {
                return true;
            }
        } else if ("3".equals(sysUser.getUserType())) {
            if (sysOrg.getOrgid().equals(orgId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验当前登录用户是否有相应用户的操作权限
     * @param userId
     * @return
     */
    public boolean validateUser(String userId) {
        SysUser sysUser = sysUserRepository.findById(userId).orElseThrow(() -> new AppException("用户不存在！"));
        return validateOrg(sysUser.getOrgId(), sysUser.getUserType());
    }

}
