package com.github.wxisme.bloomfilter.bitset;

import com.github.wxisme.bloomfilter.common.BloomFilter;
import redis.clients.jedis.*;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RedisBitSetTest {


    public static void main(String[] args) {

        //Don't forget auth password, you better use the configured redis client connection.
        //It should be noted that bloomfilter is not responsible for closing and returning redis connection resources.

//        jedis.auth("1234");
//        Jedis jedis = new Jedis("192.168.0.223", 3790);
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        JedisPool pool = new JedisPool(config, "192.168.1.202", 6379, 2000, null, 8);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 3600, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(1), new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 63; i++) {
            BloomFilter<String> filter = new BloomFilter<String>(0.0001, 20000000);
            filter.bind(new RedisBitSet(pool, "bloomfilter:key:name_1"));
            executor.execute(new BoolmRunnable(filter, pool, i));
        }
//        new BoolmRunnable(filter,pool,0).run();
    }
}

class BoolmRunnable implements Runnable {

    private BloomFilter<String> filter;

    private JedisPool pool;

    private int threadIndex;

    public BoolmRunnable(BloomFilter<String> filter, JedisPool pool, int threadIndex) {
        this.filter = filter;
        this.pool = pool;
        this.threadIndex = threadIndex;
    }

    @Override
    public void run() {
        String str;
        Jedis jedis = RedisBitSet.getJedis(pool);
        Pipeline pipeline = jedis.pipelined();
        long l= System.currentTimeMillis();
        try {
            for (int i = 0; i < 100000; i++) {
                str = UUID.randomUUID().toString();
                if (!filter.contains(str)) {
                    ((RedisBitSet)filter.getBitSet()).setPipeline(pipeline);
                    filter.add(str);
                    pipeline.incrBy("bloomfilter_count_1", 1);
                } else {
                    System.out.println(str);
                }
                if (i % 10000 == 0) {
                    pipeline.sync();
                    pipeline=jedis.pipelined();
                    System.out.println(System.currentTimeMillis()-l);
                    l=System.currentTimeMillis();
                }
                if (i % 100000 == 0) {
                    System.out.println(threadIndex + "........." + i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pipeline.sync();
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
