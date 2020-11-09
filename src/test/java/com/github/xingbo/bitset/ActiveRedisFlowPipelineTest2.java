package com.github.xingbo.bitset;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ActiveRedisFlowPipelineTest2 {

    public static void main(String[] args) throws Exception {


        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(30);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(3000);
        config.setTestOnBorrow(true);

        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setPoolConfig(config);
        factory.setHostName("192.168.1.125");
        factory.setPort(6399);
        //factory.setPort(3790);
        factory.setDatabase(14);
        factory.setTimeout(180000);
        factory.afterPropertiesSet();


        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);

        Set<String> set = new HashSet <>();
        for (int j = 0; j < 10; j++){
            set.add("ad_2019_04_01_" + j +"_PULL");
        }

        List<Future> list = new ArrayList<Future>();
        ExecutorService executorService = Executors.newFixedThreadPool(300);
        int i = 2000;
        Date date1 = new Date();
        while (i > 0) {
            Future<Object> submit = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() {
                    redisTemplate.executePipelined(new RedisCallback() {
                        @Override
                        public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                            for(String sing:set) {
                                redisConnection.incrBy(sing.getBytes(),1);
                            }
                            return null;
                        }
                    });
                    return null;
                }
            });
            list.add(submit);
            i--;
        }
        executorService.shutdown();

        while (true) {
            if (executorService.isTerminated()) {
                long time = System.currentTimeMillis() - date1.getTime();
                System.out.println("程序结束了，总耗时：" + time + " ms(毫秒)！\n");
                break;
            }
        }

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
