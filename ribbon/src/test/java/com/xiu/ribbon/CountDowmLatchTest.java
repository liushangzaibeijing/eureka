package com.xiu.ribbon;


import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDowmLatchTest {


   public static void main(String[] args) throws InterruptedException {

       Thread thread = new Thread(new MyThreadTwo());
       thread.start();

    }


    static class  MyThreadTwo  implements Runnable{

        @Override
        public void run() {
            CountDownLatch countDownLatch = new CountDownLatch(2);

            Runnable worker = new MyThread(countDownLatch);
            Runnable worker2 = new MyThread(countDownLatch);
            Runnable worker3 = new MyThread(countDownLatch);


            ExecutorService executor = Executors.newCachedThreadPool();
            executor.execute(worker);
            executor.execute(worker2);
            executor.execute(worker3);

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"全部完成");
            executor.shutdown();

        }
    }



     static class MyThread implements  Runnable{
        private CountDownLatch countDownLatch;

        public MyThread(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "執行開始！");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"run run run");
            countDownLatch.countDown();


        }
    }


}
