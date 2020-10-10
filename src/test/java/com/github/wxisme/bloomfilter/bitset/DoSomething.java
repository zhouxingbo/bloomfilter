package com.github.wxisme.bloomfilter.bitset;


import java.util.concurrent.locks.ReentrantLock;

public class DoSomething implements Runnable {

    ReentrantLock lock = new ReentrantLock();

    public void run() { // 实现run方法
        for (int i = 0; i < 1; i++) { // 重复5次
            System.out.println("次线程do something");
                try {
                    Thread.sleep(1000);
                }catch(InterruptedException ex)
                {
                    Thread.interrupted();
                }

        }
    }
    public static void main(String[] args) {
//        DoSomething dothing = new DoSomething();
//        Thread t1 = new Thread(dothing);
//        t1.start(); //这里就是楼主提的问题，启动线程，执行上面写的run()方法
//        t1.interrupt();

        DoSomething dst = new DoSomething();
        Thread t2 = new Thread(dst.createTask(),"F");
        Thread t3 = new Thread(dst.createTask(),"S");
        t2.start();
        t3.start();
        t3.interrupt();

    }

    Runnable createTask(){
        return new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        if(lock.tryLock()){
                            try {
                                System.out.println("lock" + Thread.currentThread().getName());
                                Thread.sleep(1000);
                            }finally {
                                System.out.println("un lock" + Thread.currentThread().getName());

                                lock.unlock();
                            }
                            break;
                        }else{
                            System.out.println("unable to lock" + Thread.currentThread().getName());
                        }
                    }catch (InterruptedException e){
                        System.out.println("Interrupted to lock" + Thread.currentThread().getName());
                    }

                }
            }
        };
    }
}