package com.mayer.framework;

import java.util.ArrayList;
import java.util.List;

class ThreadPool {
    private final List<Thread> threads;
    private final List<Runnable> tasks;
    private boolean isStopped;
    
    public ThreadPool(int maxThreads) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("Max threads must be greater than 0");
        }
        threads = new ArrayList<>();
        tasks = new ArrayList<>();
        isStopped = false;

        for (int i = 0; i < maxThreads; i++) {
            createThread();
        }
    }

    private void createThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                Runnable task = null;

                synchronized (tasks) {
                    while (tasks.isEmpty() && !isStopped) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                    }

                    if (tasks.isEmpty()) {
                        break; 
                    }

                    task = tasks.remove(0);
                }

                task.run();
            }

            synchronized (threads) {
                threads.remove(Thread.currentThread()); 
            }
        });

        synchronized (threads) {
            thread.start();
            threads.add(thread);
        }
    }


    public void execute(Runnable task) {
        if (isStopped)
            throw new IllegalStateException("ThreadPool stopped. Cannot execute new tasks!");

        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    public synchronized void shutdown() {
        isStopped = true;
        threads.forEach(Thread::interrupt);
    }

    public synchronized boolean isShutdown() {
        return isStopped;
    }
}