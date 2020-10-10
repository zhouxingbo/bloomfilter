package com.github.wxisme.bloomfilter.bitset;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DruidKafkaSend {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static ExecutorService executorService = Executors.newFixedThreadPool(30);

    public static void main(String[] args) throws IOException {

        Long l = System.currentTimeMillis();
        InputStream is = new FileInputStream("/home/bobo/down/pulllive_one_2.json");//request_user_active_20190115.csv
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String str ;

        Properties props = new Properties();
        //props.put("metadata.broker.list","192.168.0.55:9092");
        props.put("metadata.broker.list","192.168.0.146:9092,192.168.0.147:9092,192.168.0.114:9092");
        props.put("serializer.class","kafka.serializer.StringEncoder");
        ProducerConfig producerConfig = new ProducerConfig(props);
        Producer<String, String> stringStringProducer = new Producer<String, String>(producerConfig);

        List<String> list = new ArrayList<>();
        while ((str = br.readLine()) != null) {
            list.add(str);
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (String data : list){
                    stringStringProducer.send(new KeyedMessage<String, String>("topic_pull_live_druid",data));
                }
            }
        });
        executorService.shutdown();

        System.out.println("耗时:" + (System.currentTimeMillis() - l));




    }

}
