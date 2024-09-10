package com.insigma.sys.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jinw
 * @version 2020/2/7
 * <p>epsoft - insiis7</p>
 */
@ConfigurationProperties(prefix = "sso")
@Data
public class SSOConfigProperties {

    private String appName;
    private String authServiceRootUrl;
    private String authLogonPageRootUrl;
    private String clientAuthClass = "com.insigma.sys.common.OdinClientAuth";
    private String freeUrls;
    private String authMode;
    private String authServiceContextPath;
    private String logoutOrFailedExceptionHandler;

}
