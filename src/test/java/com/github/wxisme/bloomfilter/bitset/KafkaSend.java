package com.github.wxisme.bloomfilter.bitset;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.*;
import java.util.*;


public class KafkaSend {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        long start = System.currentTimeMillis();
        InputStream is = new FileInputStream("/home/bobo/down/66");//request_user_active_20190115.csv
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String str ;
        String[] values ;

        Properties props = new Properties();
        props.put("metadata.broker.list","192.168.0.237:9092,192.168.0.238:9092,192.168.0.239:9092");
        props.put("serializer.class","kafka.serializer.StringEncoder");
        ProducerConfig producerConfig = new ProducerConfig(props);
        Producer<String, String> stringStringProducer = new Producer<String, String>(producerConfig);

        ObjectMapper mapper = new ObjectMapper();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019,2,20);
        List<Map<String,String>> list = new ArrayList<>();
        int cc=0;

        while ((str = br.readLine()) != null) {
            cc++;
            if (cc % 1000000 == 0) {
                System.out.println("cc:"+cc);
            }

            long l = System.currentTimeMillis();
            values = str.split(",");

            Map<String,String> map = new HashMap();
            values = str.split(",");
            map.put("appId",values[0]);
            map.put("channelId",values[1]);
            map.put("subChannelId",values[2]);
            map.put("mid",values[3]);


            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(values[4]));
            map.put("createTime",String.valueOf(calendar.getTime().getTime()));

            list.add(map);
            if(list.size() > 50){
                String json = mapper.writeValueAsString(list);
                System.out.println(json);
                //stringStringProducer.send(new KeyedMessage<String, String>("user_msg_xx",json));
                list.clear();
            }


        }
        String json = mapper.writeValueAsString(list);
        System.out.println(json);
        //stringStringProducer.send(new KeyedMessage<String, String>("user_msg_xx",json));

        System.out.println("总耗时" + (System.currentTimeMillis() - start));

    }

}
