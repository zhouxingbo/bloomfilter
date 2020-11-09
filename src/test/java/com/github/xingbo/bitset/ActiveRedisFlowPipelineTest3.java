package com.github.xingbo.bitset;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ActiveRedisFlowPipelineTest3 {

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
        factory.setDatabase(15);
        factory.setTimeout(180000);
        factory.afterPropertiesSet();


        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);

        Long time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            redisTemplate.opsForValue().increment("pipline", 1);
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                for (int i = 0; i < 10000; i++) {
                    redisTemplate.opsForValue().increment("pipline", 1L);
                }
                return null;
            }
        });
        System.out.println("耗时：" + (System.currentTimeMillis() - time));

    }

}
