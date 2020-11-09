package com.github.xingbo.bitset;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ActiveRedisFlowTest {

    public static void main(String[] args) throws Exception {

        Date date1 = new Date();

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(3000);
        config.setTestOnBorrow(true);

        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setPoolConfig(config);
        factory.setHostName("192.168.0.28");
        factory.setPort(6379);
        factory.setDatabase(14);
        factory.setTimeout(180000);
        factory.afterPropertiesSet();


        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        Random random = new Random();

        List<Future> list = new ArrayList<Future>();
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        int i = 5000;
        while (i > 0) {
            Future<Object> submit = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() {
                    int id = 0;
                    for (int j = 0; j <= 50; j++){
                        id = random.nextInt(50);
                        redisTemplate.opsForValue().increment("ad_2019_04_01_" + id, 1);
                    }
                    return id;
                }
            });
            list.add(submit);
            i--;
        }
        executorService.shutdown();

        for (Future<Object> o : list){
            Object o1 = o.get();
            System.out.println("result-" + o1);
        }

        Date date2 = new Date();
        System.out.println("----程序结束运行----，程序运行时间【"
                + (date2.getTime() - date1.getTime()) + "毫秒】");

    }


}
