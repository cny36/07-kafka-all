package com.cny.springbootkafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    /**
     * 消费异常处理
     *
     * @return
     */
    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
        return (message, exception, consumer) -> {
            log.error("消费出现异常: " + exception);
            return null;
        };
    }

    /**
     * 覆盖默认的分区器
     *
     * @param producerFactory
     * @return
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        // 覆盖默认配置项
        Map<String, Object> configOverride = new HashMap<>();
        configOverride.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, MyPartitioner.class);

        // 构建KafkaTemplate对象
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory, configOverride);
        return kafkaTemplate;

    }

}
