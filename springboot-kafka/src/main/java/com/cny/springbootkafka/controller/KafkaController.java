package com.cny.springbootkafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("kafka")
public class KafkaController {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @RequestMapping("send/{msg}")
    public String send(@PathVariable String msg){
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("topic3", msg);
        //获取消息的发送结果，可以有异步和同步的方式，一般我们采用异步

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("发送失败", ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("发送成功 topic:{}", metadata.topic());
            }
        });
        return "success";
    }
}
