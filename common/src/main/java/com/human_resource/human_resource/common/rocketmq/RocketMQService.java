package com.human_resource.human_resource.common.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

public interface RocketMQService {
    DefaultMQPushConsumer getConsumer(String consumerGroup);
}