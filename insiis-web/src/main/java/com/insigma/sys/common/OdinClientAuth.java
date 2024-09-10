package com.insigma.sys.common;

import com.insigma.framework.util.SysUtils;
import com.insigma.odin.framework.est.EstException;
import com.insigma.odin.framework.est.auth.ClientAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jinw
 * @version 2020/2/7
 * <p>epsoft - insiis7</p>
 */
public class OdinClientAuth implements ClientAuth {

    private static final Logger log = LoggerFactory.getLogger(OdinClientAuth.class);

    @Override
    public boolean syslogon(String loginName, HttpServletRequest request) throws EstException {
        UserDetailsService userDetailsService;
        ApplicationContext applicationContext = WebApplicationContextUtils.findWebApplicationContext(request.getServletContext());
        if (SysUtils.getAppContext() != null) {
            userDetailsService = SysUtils.getBean(UserDetailsService.class);
        } else {
            userDetailsService = applicationContext.getBean(UserDetailsService.class);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginName);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        token.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        request.getSession().setAttribute(OdinClientAuthExtendFilter.IS_SAVE_LOGONLOG_SESSION_KEY, Boolean.TRUE);
        return true;
    }

    @Override
    public boolean syslogout(String loginName, HttpServletRequest request, HttpServletResponse response) throws EstException {
        LogoutHandler logoutHandler;
        if (SysUtils.getAppContext() != null) {
            logoutHandler = SysUtils.getBean("myLogoutHandler", LogoutHandler.class);;
        } else {
            logoutHandler = WebApplicationContextUtils.findWebApplicationContext(request.getServletContext()).getBean("myLogoutHandler",LogoutHandler.class);
        }
        SecurityContext securityContext = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContext != null) {
            logoutHandler.logout(request, response, securityContext.getAuthentication());
            request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
        }
        request.getSession().invalidate();
        return true;
    }
}
