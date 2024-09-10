package com.insigma.sys.common.impl;

import com.insigma.framework.exception.UserNoLoginException;
import com.insigma.framework.web.securities.entity.SysUserDetails;
import com.insigma.sys.common.CurrentUserService;
import com.insigma.sys.entity.SysUser;
import com.insigma.sys.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created by Administrator on 2019/1/15.
 */
@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    public SysUser getCurrentUser() {
        SysUser sysUser = currentUserFromSecurityContext();
        if (sysUser == null) {
            throw new UserNoLoginException("获取不到当前登录用户信息！");
        }
        return sysUser;
    }

    /**
     * 从SpringSecurity中获取当前用户信息 jinw
     *
     * @return
     */
    private SysUser currentUserFromSecurityContext() {
        SysUser sysUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            if (authentication.getPrincipal() instanceof SysUserDetails) {
                SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
                sysUser = (SysUser) userDetails.getOthers().get("sysUser");
                if (sysUser == null) {
                    sysUser = sysUserRepository.findByLogonName(userDetails.getUsername()).get(0);
                    userDetails.getOthers().put("sysUser", sysUser);
                    HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                    request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                }
            }
        }
        return sysUser;
    }
}
