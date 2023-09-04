package com.mayer.framework;

import java.util.concurrent.CountDownLatch;

class Task implements Runnable {
	
	private CountDownLatch latch;
    
	private String name;

    public Task(String name, CountDownLatch latch) {
        this.latch = latch;
        this.name = name;
    }

    public void run() {
    	try {
    		System.out.println(name + " started wait 2s");
			Thread.sleep(2000);
			System.out.println(name + " finished wait 2s");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println("Task " + name + " is running on thread: " + Thread.currentThread().getName());
        latch.countDown();
    }
}