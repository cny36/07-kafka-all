package com.cny.springbootkafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class KafkaConsumer {

    /*@KafkaListener(topics = "topic3", groupId = "es99")
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("消费者收到消息：");
        log.info("主题:{}", record.topic());
        log.info("分区:{}", record.partition());
        log.info("value:{}", record.value());
        //手动提交offset
        ack.acknowledge();
    }*/

    /**
     * 批量获取消息
     *
     * @param records
     * @param ack
     */
    @KafkaListener(topics = "topic3", groupId = "es99", errorHandler = "consumerAwareErrorHandler")
    public void onMessage(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        log.info("批量拉取消息，消息量为：{}", records.size());
        for (ConsumerRecord<String, String> record : records) {
            log.info("主题:{}", record.topic());
            log.info("分区:{}", record.partition());
            log.info("value:{}", record.value());
        }
        //手动提交offset
        ack.acknowledge();

        // 模拟消费消息出现异常
        int i = 1 / 0;
    }

}
