package com.github.xingbo.bitset;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ActiveRedisFlowPipelineTest {

    public static void main(String[] args) throws Exception {

        Date date1 = new Date();

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(3000);
        config.setTestOnBorrow(true);

        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setPoolConfig(config);
        factory.setHostName("192.168.1.125");
        factory.setPort(6379);
        //factory.setPort(3790);
        factory.setDatabase(14);
        factory.setTimeout(180000);
        factory.afterPropertiesSet();


        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        Random random = new Random();

        List<Future> list = new ArrayList<Future>();
        ExecutorService executorService = Executors.newFixedThreadPool(70);
        int i = 5000;
        while (i > 0) {
            Future<Object> submit = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() {
                    Set<String> set = new HashSet <>();
                    int id = 0;
                    for (int j = 0; j <= 50; j++){
                        id = random.nextInt(50);
                        set.add("ad_2019_04_01_" + id);
                    }
                    redisTemplate.executePipelined(new RedisCallback() {
                        @Override
                        public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                            for(String sing:set) {
                                redisConnection.incrBy(sing.getBytes(),1);
                            }
                            return null;
                        }
                    });
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


    public static byte[] intToByte(int num){
        byte[]bytes=new byte[4];
        bytes[0]=(byte) ((num>>24)&0xff);
        bytes[1]=(byte) ((num>>16)&0xff);
        bytes[2]=(byte) ((num>>8)&0xff);
        bytes[3]=(byte) (num&0xff);
        return bytes;
    }


}
