package com.github.xingbo.bitset;

import com.github.xingbo.common.BloomFilter;
import redis.clients.jedis.*;

import java.util.*;

public class PipelineTest {


    public static void main(String[] args){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        JedisPool pool = new JedisPool(config, "192.168.1.202", 6379, 2000, null, 7);
        BloomFilter<String> filter = new BloomFilter<String>(0.0001, 1000000);
        filter.bind(new RedisBitSet(pool, "bloomfilter:key:name_1"));
        List<String> temp=new ArrayList<>();
        for(int i=0;i<1000000;i++){
            temp.add(UUID.randomUUID().toString()+",com.ecc,CLICK");
        }
        Set<String> l = new HashSet<>();
        int index=0;
        for(String str:temp){
            l.add(str);
            index++;
            if (index % 100000 == 0) {
                System.out.println(index);
                new PipelineTest().run(pool,filter, l);
                l = new HashSet<>();
            }
        }
        new PipelineTest().run(pool,filter, l);


    }

    private void run(JedisPool pool,BloomFilter<String> filter,Set<String> l ){
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
                if (index % 10000 == 0) {
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
                    values = str.split(",");
                    pipeline.incrBy("useropen_" + values[1] + "_" + values[2], 1);
                    break;
                }
            }
        }
        pipeline.sync();

    }

    /*public void run(JedisPool pool,BloomFilter<String> filter,Set<String> l) {
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
}
