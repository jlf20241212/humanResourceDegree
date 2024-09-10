package com.human_resource.human_resource.common.elasticsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "essql")
@Component
public class ESConfig {
    private String url;
    private String password;
    private String username;
    private String hosts;
    private int port;
    private String scheme;
}
