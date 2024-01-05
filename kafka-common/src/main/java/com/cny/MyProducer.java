package com.cny;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@Slf4j
public class MyProducer {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.创建参数配置的对象
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.247.5:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //1.3 设置确认机制
        //设置应答级别:多少个分区副本备份完成时向生产者发送ack确认(可选0、1、all/-1)
        //acks=0，性能最高，消息发送的可靠性最低，因为不需要等待任何一个Broker节点的回应,只需要写入缓冲区即可
        //acks=1，性能折中，消息发送的可靠性折中，等待leader将消息写入到本地log即可；
        //acks=all, 注意！min.insync.replicas(默认值为1，配置大于等于2)，设置为大于1之后，可靠性最高，性能最低；
        properties.put(ProducerConfig.ACKS_CONFIG, "0");
        //1.4 设置重试策略
        properties.put(ProducerConfig.RETRIES_CONFIG,3);
        //1.5 设置重试的时间间隔
        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG,200);
        //1.6 设置本地缓冲区
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG,33554432);
        //1.7 设置每次批量发送的消息大小
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG,16384);
        //1.8 设置消息发送的最大延迟时间
        properties.put(ProducerConfig.LINGER_MS_CONFIG,10);
        //1.7 设置压缩算法
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"gzip");

        //2.创建生产者对象
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);

        //3.创建消息对象
        ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(
                "topic1",
                0,
                "product",
                JSON.toJSONString(new ProductDTO(111, "63857546546")));

        //4.同步发送消息
        /*RecordMetadata metadata = kafkaProducer.send(producerRecord).get();
        log.info("获取到发送消息的结果如下：");
        log.info("消息的主题:{}", metadata.topic());
        log.info("消息的分区号:{}", metadata.partition());
        log.info("消息的偏移量:{}", metadata.offset());*/

        //5.异步发送消息
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        kafkaProducer.send(producerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if(e != null){
                    log.error("消息发送失败");
                }
                if(metadata != null){
                    log.info("消息的主题:{}", metadata.topic());
                    log.info("消息的分区号:{}", metadata.partition());
                    log.info("消息的偏移量:{}", metadata.offset());
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();


    }
}
