package com.mayer.framework;

import java.util.ArrayList;
import java.util.List;

class ThreadPool {
    private final List<Thread> threads;
    private final List<Runnable> tasks;
    private boolean isStopped;

    public ThreadPool() {
        threads = new ArrayList<>();
        tasks = new ArrayList<>();
        isStopped = false;
    }

    public synchronized void execute(Runnable task) {
        if (isStopped)
            throw new IllegalStateException("ThreadPool stopped. Cant execute new tasks!");

        Thread availableThread = null;

        synchronized (threads) {
            for (Thread thread : threads) {
                if (!thread.isAlive()) {
                    availableThread = thread;
                    break;
                }
            }
        }

        if (availableThread == null) {
            availableThread = new Thread(() -> {
                Runnable taskToExecute = null;

                while (true) {
                    synchronized (tasks) {
                        while (tasks.isEmpty() && !isStopped) {
                            try {
                                tasks.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (isStopped)
                            break;

                        taskToExecute = tasks.remove(0);
                    }

                    taskToExecute.run();
                }
            });

            threads.add(availableThread);
            availableThread.start();
        }

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