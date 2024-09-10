package com.insigma.sys.controller;

import com.insigma.framework.ResponseMessage;
import com.insigma.framework.web.securities.auth.SysLoginHandler;
import com.insigma.framework.web.securities.dto.BaseUserInfo;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取当前登录人
 * Created by yinjh on 2020/2/21.
 */
@RestController
@RequestMapping({"/sso", "/check"})
public class SSOController {

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SysLoginHandler sysLoginHandler;

    @PostMapping("/login")
    public ResponseMessage login(HttpServletRequest request) {
        SysUser sysUser = currentUserService.getCurrentUser();
        BaseUserInfo userInfo = new BaseUserInfo();
        userInfo.setUserid(sysUser.getUserId());
        userInfo.setUsername(sysUser.getLogonName());
        userInfo.setNickname(sysUser.getDisplayName());

        sysLoginHandler.success(userInfo.getUsername(), request);

        return ResponseMessage.ok(userInfo);
    }

}
