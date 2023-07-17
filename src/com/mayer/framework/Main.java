package com.mayer.framework;

public class Main {
    
	public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool();

        for (int i = 0; i < 10; i++) {
            Task task = new Task("Task nº: " + i);
            threadPool.execute(task);
        }

        threadPool.shutdown();
    }
}