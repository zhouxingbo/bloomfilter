package com.github.xingbo;

import com.github.xingbo.bitset.RedisBitSet;
import com.github.xingbo.common.BloomFilter;
import redis.clients.jedis.*;

import java.io.*;
import java.util.*;

public class UserActiveTest {


    public static void main(String[] args) throws IOException {

        long lll = System.currentTimeMillis();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        JedisPool pool = new JedisPool(config, "192.168.0.28", 6379, 2000, null, 14);

        InputStream is = new FileInputStream("/home/bobo/down/66");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String str = null;
        int index = 0;
//        BloomFilter<String> filter = new BloomFilter<String>(0.0001, 20000000);
//        filter.bind(new RedisBitSet(pool, "bf:key"));
        List<BloomFilter<String>> filters = new ArrayList<>();
        BloomFilter<String> filter = null;
        for (int i = 0; i < 24; i++) {
            filter = new BloomFilter<>(0.0001, 20000000);
            filter.bind(new RedisBitSet(pool, "bf:key:" + i));
            filters.add(filter);
        }

        List<Set<String>> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            list.add(new HashSet<String>());
        }
        String[] values = null;
        String defaultTime = "20190216";
        char KEY_MARK = '\u0001';
        while ((str = br.readLine()) != null) {
            values = str.split(",");
            String appId = values[0];
            String channel = values[1];
            String subchannel = values[2];
            String mid = values[3];
            String hour = values[4];
            hour = hour.compareTo("10") < 0 ? "0" + hour : hour;
            String redisKey = "ac:" +defaultTime + hour +  KEY_MARK + appId + KEY_MARK + channel + KEY_MARK +subchannel + "," + mid;
            list.get(Integer.parseInt(values[4])).add(redisKey);
            index++;
            if (index % 1000000 == 0) {
                System.out.println(" index:" + index);
                run(list, filters, pool);
            }
        }
        run(list, filters, pool);
        System.out.println(System.currentTimeMillis() - lll);
    }

    public static void run(List<Set<String>> list, List<BloomFilter<String>> filters, JedisPool pool) {
        int listIndex = 0;
        for (Set<String> l : list) {
            if (!l.isEmpty()) {
                new UserActiveBoolmRunnable(filters.get(listIndex), pool, l).run();
                l.clear();
            }
            listIndex++;
        }
    }
}

class UserActiveBoolmRunnable implements Runnable {

    private BloomFilter<String> filter;

    private JedisPool pool;

    Set<String> l = null;

    public UserActiveBoolmRunnable(BloomFilter<String> filter, JedisPool pool, Set<String> l) {
        this.filter = filter;
        this.pool = pool;
        this.l = l;
    }



    /*@Override
    public void run() {
        Jedis jedis = RedisBitSet.getJedis(pool);
        Pipeline pipeline = jedis.pipelined();
        try {
            int index=0;
            String values[];
            for (String str:l) {
                index++;
                if (!filter.contains(str)) {
                    ((RedisBitSet) filter.getBitSet()).setPipeline(pipeline);
                    filter.add(str);
                    values=str.split(",");
                    pipeline.incrBy("useropen_" + values[1]+"_"+values[2], 1);
                }
                if (index % 10000 == 0) {
                    pipeline.sync();
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
    }*/

    @Override
    public void run() {
        Jedis jedis = RedisBitSet.getJedis(pool);
        Jedis jedisRead = RedisBitSet.getJedis(pool);
        Pipeline pipeline = jedis.pipelined();
        Pipeline readPipeline = jedisRead.pipelined();

        ((RedisBitSet) filter.getBitSet()).setPipeline(pipeline);
        ((RedisBitSet) filter.getBitSet()).setReadPipeline(readPipeline);
        ((RedisBitSet) filter.getBitSet()).setResultMap(new HashMap());
        try {
            int index = 0;

            List<String> strList = new ArrayList<>();
            for (String str : l) {
                index++;
                strList.add(str);
                filter.contains(str);
                if (index % 20000 == 0) {
                    //batch contains
                    readPipeline.syncAndReturnAll();
                    this.sendValue(filter, pipeline, ((RedisBitSet) filter.getBitSet()).getResultMap(), strList);
                    strList = new ArrayList<>();
                    ((RedisBitSet) filter.getBitSet()).setResultMap(new HashMap());
                }
            }
            readPipeline.syncAndReturnAll();
            this.sendValue(filter, pipeline, ((RedisBitSet) filter.getBitSet()).getResultMap(), strList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (jedisRead != null) {
                jedisRead.close();
            }
        }
    }

    private void sendValue(BloomFilter<String> filter, Pipeline pipeline, Map<String, List<Response<Boolean>>> resultMap, List<String> strList) {
        List<Response<Boolean>> responseList;
        for (String str : strList) {
            responseList = resultMap.get(str);
            for (Response<Boolean> response : responseList) {
                if (response.get().toString().equals("false")) {
                    filter.add(str);
                    pipeline.incrBy(str.substring(0, str.lastIndexOf(",")), 1);
                    break;
                }
            }
        }
        pipeline.sync();

    }
}
