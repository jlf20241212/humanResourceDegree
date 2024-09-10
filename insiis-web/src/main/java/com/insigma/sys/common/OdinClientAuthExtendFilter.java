package com.insigma.sys.common;

import com.insigma.framework.util.SysUtils;
import com.insigma.framework.util.UserAgentUtil;
import com.insigma.framework.web.securities.commons.SecurityUtils;
import com.insigma.framework.web.securities.entity.SysLogonLog;
import com.insigma.framework.web.securities.entity.SysUserDetails;
import com.insigma.framework.web.securities.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author jinw
 * @version 2020/2/13
 * <p>epsoft - insiis7</p>
 */
@Component
@WebFilter(filterName = "odinClientExtendFilter",urlPatterns = "/*")
@Slf4j
public class OdinClientAuthExtendFilter extends OncePerRequestFilter {

    public static final String IS_SAVE_LOGONLOG_SESSION_KEY = "save_logonlog_flag";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session =  httpServletRequest.getSession();
        Boolean isSaveLogonlog = null;
        try {
            isSaveLogonlog = (Boolean) session.getAttribute(IS_SAVE_LOGONLOG_SESSION_KEY);
            if (isSaveLogonlog != null && isSaveLogonlog.booleanValue()) {
                SysLogonLog logonLog = SysUtils.getBean(SysLogService.class).saveLogonLog(SecurityUtils.getIpAddr(httpServletRequest), session.getId(), UserAgentUtil.getBrowser(httpServletRequest), UserAgentUtil.getSystem(httpServletRequest));
                SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (userDetails != null) {
                    userDetails.getOthers().put("logid", logonLog.getLogonlogid());
                }
            }
        } catch (Exception e) {
            log.error("记录登录日志失败！", e);
        } finally {
            if (isSaveLogonlog != null) {
                session.removeAttribute(IS_SAVE_LOGONLOG_SESSION_KEY);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
