package com.github.wxisme.bloomfilter.bitset;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadRedis {

    public static void main(String [] args){
//        JedisPoolConfig config = new JedisPoolConfig();
//        config.setMaxTotal(100);
//        JedisPool pool = new JedisPool(config, "192.168.1.202", 6379, 2000, null, 7);
//        Jedis jedis = RedisBitSet.getJedis(pool);
//
//
//        Set<String> keys=jedis.keys("active_*");
//        String[] strs;
//        for(String s:keys){
//            strs=s.split("_");
//            System.out.println("insert into test.active_test_calc values('"+strs[1]+"','"+strs[2]+"',"+strs[3]+","+jedis.get(s)+");");
//        }

        String str = "going to do dsp request. requestId: 3e8b436d-3b12-435c-957b-74583b987efb,";
        String reg = "requestId: (.*?)\\,";
        Pattern pattern = Pattern.compile(reg);

        Matcher matcher = pattern.matcher(str);

        if( matcher.find() ){
            // 包含前后的两个字符
            System.out.println( matcher.group(1) );
        }
    }
}
