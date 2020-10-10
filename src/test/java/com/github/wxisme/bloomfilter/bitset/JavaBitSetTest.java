package com.github.wxisme.bloomfilter.bitset;

import com.github.wxisme.bloomfilter.common.BloomFilter;

import java.util.*;

public class JavaBitSetTest {

    public static void main(String[] args) {
        //(falsePositiveProbability, expectedNumberOfElements)
        BloomFilter<String> filter = new BloomFilter<String>(0.0001, 10000000);
        filter.bind(new JavaBitSet());


        List<String> list=new ArrayList<String>();
        for(int i=0;i<10000000;i++){
            list.add(UUID.randomUUID().toString());
        }
        Set<String> set=new HashSet<String>();
        long l=System.currentTimeMillis();
        for(String str:list){
            set.add(str);
            if(!filter.contains(str)) {
                filter.add(str);
            }
        }
        System.out.println("cost"+(System.currentTimeMillis()-l));
        System.out.println(filter.count()+"-----"+set.size());


    }

}
