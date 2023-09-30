package com.mayer.framework;

import java.util.concurrent.CountDownLatch;

public class Test {
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		//ThreadPool threadPool = new ThreadPool(4);
        ConcurrentThreadPool threadPool = new ConcurrentThreadPool(4);
        //LinkedBlockingThreadPool threadPool = new LinkedBlockingThreadPool(2);
		//ThreadPoolExecutorWithReenqueuePolicy threadPool = new ThreadPoolExecutorWithReenqueuePolicy(1, 2, 60, 10);
		
        CountDownLatch latch = new CountDownLatch(1000);

        for (int i = 0; i < 1000; i++) {
            Task task = new Task("Task nº: " + i, latch);
            threadPool.execute(task);
        }

        try {
            latch.await();  
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        threadPool.shutdown();
        long finish = System.currentTimeMillis();
        
        long timeElapsed = finish - start;
        System.out.println("##################");
        System.out.println(timeElapsed);
    }

}
