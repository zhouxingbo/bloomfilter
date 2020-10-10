package com.test;

import com.github.wxisme.bloomfilter.typeclass.test;
import lombok.Data;

import java.util.*;
import java.util.concurrent.*;

public class Test {


    public static void main(String[] args) throws Exception {
        Test test = new Test();
        int i=0;
        while(true){  
            System.out.println("Hello world !"+i);  
            i++;  
            Thread.sleep(100);  
        }

    }

    public Map<Integer, Good> futureGood() throws Exception{
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        List<Callable<Good>> resultList = new ArrayList<>();
        for (int j = 0; j < 10; j ++) {
            int rand = 10;

            FactorialCalculator factorialCalculator = new FactorialCalculator(rand);
            resultList.add(factorialCalculator);
        }
        List<Future<Good>> futures = executor.invokeAll(resultList);
        Map<Integer, Good> map = new HashMap<>();
        for (int i = 0; i < futures.size(); i++) {
            Future<Good> mapFuture = futures.get(i);
            Good good = mapFuture.get();
            map.put(good.getId(),good);
        }
        return map;
    }
}



class FactorialCalculator implements Callable<Good> {

    private Integer number;

    public FactorialCalculator(Integer number) {
        this.number = number;
    }
    public Good call() throws Exception {
        int result = 1;

        if (number == 0 || number == 1) {
            result = 1;
        }else {
            for (int i = 2; i < number; i++) {
                result *= i;
                TimeUnit.MICROSECONDS.sleep(200);
                if (i == 5) {
                    throw new IllegalArgumentException("excepion happend");//计算5以上的阶乘都会抛出异常. 根据需要注释该if语句
                }
            }
        }
        System.out.printf("%s: %d\n", Thread.currentThread().getName(), result);
        Good good = new Good();
        good.setId(result);
        good.setName("good" + result);
        return good;
    }
}
@Data
class Good{
    private int id;
    private String name;
}