package com.insigma.sys.common.impl;

import com.insigma.framework.data.firewall.permission.validate.ValidateKeyValueStore;
import com.insigma.framework.exception.AppException;
import com.insigma.framework.web.securities.auth.SysLoginHandler;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.service.SysHashService;
import com.insigma.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class MySysLoginHandler implements SysLoginHandler {

    @Value("${sys.hash.enabled:false}")
    private boolean hashEnabled;

    @Autowired
    private SysHashService sysHashService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ValidateKeyValueStore validateKeyValueStore;

    @Override
    public void success(String username, HttpServletRequest request) {
        request.getSession().setAttribute("auth_filter_ignored",Boolean.TRUE);
        log.info("{} 登录成功!", username);
        if (hashEnabled) {
            SysUser sysUser = sysUserService.findUserByLogonName(username);
            if (!sysHashService.checkHash(sysUser)) {
                throw new AppException("用户数据不合法！");
            }
        }


        // 测试数据与安全
        SysUser sysUser = sysUserService.findUserByLogonName(username);
        validateKeyValueStore.set("orgid", String.valueOf(sysUser.getOrgId()));
        if ("admin".equals(sysUser.getLogonName())) {
            validateKeyValueStore.skip();
        }
    }

    @Override
    public void failure(String username, HttpServletRequest request) {
        log.info("{} 登录失败!", username);
    }
}
