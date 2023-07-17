package com.mayer.framework;

class Task implements Runnable {
    
	private String name;

    public Task(String name) {
        this.name = name;
    }

    public void run() {
        System.out.println("Task " + name + " is running on thread: " + Thread.currentThread().getName());
    }
}