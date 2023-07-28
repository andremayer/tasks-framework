package com.mayer.framework;

public class Main {
    
	public static void main(String[] args) {
        //ThreadPool threadPool = new ThreadPool();
        ConcurrentThreadPool threadPool = new ConcurrentThreadPool(3);

        for (int i = 0; i < 5000; i++) {
            Task task = new Task("Task nº: " + i);
            threadPool.execute(task);
        }

        threadPool.shutdown();
    }
}