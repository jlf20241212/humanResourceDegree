package com.insigma.sys.util;

import com.insigma.framework.util.SysUtils;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.SysHashService;
import com.insigma.sys.service.SysUserService;

import java.util.List;

/**
 * @author yinjh
 * @version 2021/8/19
 */
public class SysHashUtil {

    /**
     * 重新计算所有用户的hash
     */
    public static void reComputeAllUserHash() {
        SysUserService sysUserService = SysUtils.getBean(SysUserService.class);
        List<SysUser> list = sysUserService.queryAllUser();
        SysHashService sysHashService = SysUtils.getBean(SysHashService.class);
        sysHashService.saveAllHash(list);
    }

    /**
     * 重新计算指定用户的hash
     * @param logonName
     */
    public static void reComputeUserHash(String logonName) {
        SysUserService sysUserService = SysUtils.getBean(SysUserService.class);
        SysUser sysUser = sysUserService.findUserByLogonName(logonName);
        SysHashService sysHashService = SysUtils.getBean(SysHashService.class);
        sysHashService.saveHash(sysUser);
    }

}
