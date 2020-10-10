package com.github.wxisme.bloomfilter.bitset;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ActiveJRedisFlowPipelineTest {

    public static void main(String[] args) throws Exception {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(30);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(3000);
        config.setTestOnBorrow(true);

        JedisPool pool = new JedisPool(config, "192.168.1.125", 6399, 2000, null, 14);

        Set<String> set = new HashSet <>();
        for (int j = 0; j < 10; j++){
            set.add("ad_2019_04_01_" + j +"_PULL");
        }

        List<Future> list = new ArrayList<Future>();
        ExecutorService executorService = Executors.newFixedThreadPool(300);
        int i = 5000;
        Date date1 = new Date();
        while (i > 0) {
            Future<Object> submit = executorService.submit(new Callable<Object>() {
                @Override
                public Object call() {
                    // 开启流水线
                    Jedis jedis = pool.getResource();
                    Pipeline pipeline = jedis.pipelined();
                    for (String sing : set){
                        pipeline.incrBy(sing, 1);
                    }
                    // 只执行同步但不返回结果
                    pipeline.sync();
                    // 以list的形式返回执行过的命令的结果
                    //List<Object> result = pipeline.syncAndReturnAll();
                    jedis.close();
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


}
