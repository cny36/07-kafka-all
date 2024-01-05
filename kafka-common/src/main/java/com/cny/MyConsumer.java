package com.cny;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class MyConsumer {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.247.5:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "ES");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        //1.4 设置offset的提交方式，默认为自动提交（每隔5秒自动提交一次）
        //建议设置为手动提交，可以更好确保消息已经被处理完毕
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");


        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        //3.订阅主题
        consumer.subscribe(Arrays.asList("topic1"));

        //4.开始拉取消息进行消费
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            //处理消息
            for (ConsumerRecord<String, String> record : records) {
                log.info("消息的偏移量：{}",record.offset());
                log.info("消息的key：{}",record.key());
                log.info("消息的value：{}",record.value());
            }

            //提交偏移量
            consumer.commitSync();

        }

    }
}
