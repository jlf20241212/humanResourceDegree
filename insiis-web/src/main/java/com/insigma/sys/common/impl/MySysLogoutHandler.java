package com.insigma.sys.common.impl;

import com.insigma.framework.web.securities.auth.ExtSysLogoutHandler;
import com.insigma.odin.framework.est.service.SSOServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yinjh
 * @version 2022/5/10
 * @since 2.6.5
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sso", name = "app-name", matchIfMissing = false)
public class MySysLogoutHandler implements ExtSysLogoutHandler {

    @Value("${sso.app-name:}")
    private String appName;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String est = (String) request.getSession().getAttribute("est");
        if (est != null) {
            log.info("存在est令牌，执行注销操作！est: {}, appName: {}", est, appName);
            SSOServiceFactory.logoutService().logout(est, appName, request);
        } else {
            log.info("无est令牌，不执行注销操作！");
        }
    }
}
