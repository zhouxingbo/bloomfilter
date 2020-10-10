package com.github.wxisme.bloomfilter.bitset;

import com.github.wxisme.bloomfilter.common.BloomFilter;
import redis.clients.jedis.*;

import java.io.*;
import java.util.*;

public class SynUserOpenAppTest {


    public static void main(String[] args) throws IOException {

        long lll=System.currentTimeMillis();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        JedisPool pool = new JedisPool(config, "192.168.1.202", 6379, 2000, null, 8);

        InputStream is = new FileInputStream("/home/bobo/down/aa");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String str = null;
        Set<String> l = new HashSet<>();
        int index = 0;
        BloomFilter<String> filter = new BloomFilter<String>(0.0001, 20000000);
        filter.bind(new RedisBitSet(pool, "bf:key:"));
//        List<BloomFilter<String>> filters=new ArrayList<>();
//        for(int i=0;i<24;i++){
//            filters.add(new BloomFilter<>(0.0001, 20000000));
//            filter.bind(new RedisBitSet(pool, "bf:key:"+i));
//        }
        int cc=0;
        String []values=null;
        while ((str = br.readLine()) != null) {
            cc++;
            if (cc % 1000000 == 0) {
                System.out.println("cc:"+cc);
            }
            if(str.indexOf("SK1139,0003")>=0&&str.endsWith(",0")) {
                values = str.split(",");
                l.add(values[0] + "," + values[1] + "," + values[2] + "," + values[3]);
                index++;
                if (index % 1000000 == 0) {
                    System.out.println(index);
                    new SynUserOpenBoolmRunnable(filter, pool, l).run();
                    l = new HashSet<>();
                }
           }
        }
        new SynUserOpenBoolmRunnable(filter, pool, l).run();
        System.out.println(System.currentTimeMillis()-lll);
    }
}

class SynUserOpenBoolmRunnable implements Runnable {

    private BloomFilter<String> filter;

    private JedisPool pool;

    Set<String> l = null;

    public SynUserOpenBoolmRunnable(BloomFilter<String> filter, JedisPool pool, Set<String> l) {
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
            int index=0;

            List<String> strList=new ArrayList<>();
            for (String str:l) {
                index++;
                strList.add(str);
                filter.contains(str);
                if (index % 20000 == 0) {
                    //batch contains
                    readPipeline.syncAndReturnAll();
                    this.sendValue(filter,pipeline,((RedisBitSet) filter.getBitSet()).getResultMap(),strList);
                    strList=new ArrayList<>();
                    ((RedisBitSet) filter.getBitSet()).setResultMap(new HashMap());
                }
            }
            readPipeline.syncAndReturnAll();
            this.sendValue(filter,pipeline,((RedisBitSet) filter.getBitSet()).getResultMap(),strList);

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

    private void sendValue(BloomFilter<String> filter,Pipeline pipeline, Map<String, List<Response<Boolean>>> resultMap, List<String> strList){
        String values[];
        List<String> result=new ArrayList<>();
        List<Response<Boolean>> responseList=null;
        for(String str:strList){
            responseList=resultMap.get(str);
            for(Response<Boolean> response :responseList){
                if(response.get().toString().equals("false")){

                    filter.add(str);
                    System.out.println(str);
                    values = str.split(",");
                    pipeline.incrBy("active_" + values[1] + "_" + values[2], 1);
                    break;
                }
            }
        }
        pipeline.sync();

    }
}
