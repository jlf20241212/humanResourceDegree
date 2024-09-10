package com.insigma.sys.config;

import com.insigma.framework.web.securities.config.SysSecurityConfig;
import com.insigma.framework.web.securities.config.SysSecurityNoopConfig;
import com.insigma.odin.framework.est.auth.GenericAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinw
 * @version 2020/2/7
 * <p>epsoft - insiis7</p>
 */
@Configuration
@EnableConfigurationProperties(SSOConfigProperties.class)
@AutoConfigureAfter({SysSecurityConfig.class, SysSecurityNoopConfig.class})
@ConditionalOnProperty(prefix = "sso", name = "app-name", matchIfMissing = false)
public class SSOConfig {

    @Autowired
    private SSOConfigProperties configProperties;

    @Bean
    public FilterRegistrationBean genericAuth() {
        FilterRegistrationBean<GenericAuthFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new GenericAuthFilter());
        Map<String, String> initParameters = new HashMap<>();
        /*initParameters.put("app-name", "odin7c");
        initParameters.put("auth-service-root-url", "http://127.0.0.1:8080/");
        initParameters.put("client-auth-class", "com.insigma.sys.common.OdinClientAuth");
        initParameters.put("free-urls", "/login");*/
        initParameters.put("app-name", configProperties.getAppName());
        initParameters.put("auth-service-root-url", configProperties.getAuthServiceRootUrl());
        initParameters.put("auth-logon-page-root-url", configProperties.getAuthLogonPageRootUrl());
        initParameters.put("client-auth-class", configProperties.getClientAuthClass());
        initParameters.put("free-urls", configProperties.getFreeUrls());
        initParameters.put("auth-mode", configProperties.getAuthMode());
        if (!ObjectUtils.isEmpty(configProperties.getAuthServiceContextPath())) {
            initParameters.put("auth-service-context-path", configProperties.getAuthServiceContextPath());
        }
        if (!ObjectUtils.isEmpty(configProperties.getLogoutOrFailedExceptionHandler())) {
            initParameters.put("logout-or-failed-exception-handler", configProperties.getLogoutOrFailedExceptionHandler());
        }
        filterRegistrationBean.setInitParameters(initParameters);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(-100);
        return filterRegistrationBean;
    }

}
