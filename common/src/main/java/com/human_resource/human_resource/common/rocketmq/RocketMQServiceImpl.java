package com.human_resource.human_resource.common.rocketmq;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RocketMQServiceImpl implements RocketMQService {
    private String serverAddr = null;

    public RocketMQServiceImpl(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    Map<String, DefaultMQPushConsumer> consumerMap = new ConcurrentHashMap<>();

    @Override
    @Synchronized
    public DefaultMQPushConsumer getConsumer(String consumerGroup) {
        DefaultMQPushConsumer defaultMQPushConsumer = consumerMap.get(consumerGroup);
        if (defaultMQPushConsumer != null) {
            return defaultMQPushConsumer;
        }
        DefaultMQPushConsumer instance = new DefaultMQPushConsumer(consumerGroup);
        instance.setNamesrvAddr(this.serverAddr);
        this.consumerMap.put(consumerGroup, instance);
        return instance;

    }
}