package com.human_resource.human_resource.common.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;

import com.human_resource.human_resource.common.config.MqConfig;
import com.human_resource.human_resource.common.rocketmq.RocketMQService;
import com.human_resource.human_resource.common.rocketmq.RocketMQServiceImpl;

@Configuration
public class RocketMqAutoConfiguration {
    @Resource
    private MqConfig mqConfig;
    @Bean
    RocketMQService createRocketMQService() {
        RocketMQServiceImpl instance = new RocketMQServiceImpl(mqConfig.getNameServer());
        return instance;
    }
}