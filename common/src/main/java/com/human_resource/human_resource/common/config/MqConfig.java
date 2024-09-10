package com.human_resource.human_resource.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class MqConfig {
    private String nameServer;
    private String topic;
    private String consumerGroup;
}