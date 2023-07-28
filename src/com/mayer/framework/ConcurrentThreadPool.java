package com.mayer.framework;

import java.util.concurrent.ConcurrentLinkedQueue;

/** Ref:
	https://www.baeldung.com/java-concurrent-queues#7-concurrentlinkedqueue
	https://www.baeldung.com/java-queue-linkedblocking-concurrentlinked#concurrentlinkedqueue
*/
public class ConcurrentThreadPool {

	private ConcurrentLinkedQueue<Runnable> tasks;
    private Thread[] threads;
    private boolean isStopped;

    public ConcurrentThreadPool(int numThreads) {
        tasks = new ConcurrentLinkedQueue<>();
        threads = new Thread[numThreads];
        isStopped = false;

        for (int i = 0; i < numThreads; i++) {
            createThread(i);
        }
    }

    private void createThread(int index) {
        threads[index] = new Thread(() -> {
            while (!isStopped || !tasks.isEmpty()) {
                Runnable taskToExecute = tasks.poll();
                if (taskToExecute != null) {
                    taskToExecute.run();
                }
            }
        });

        threads[index].start();
    }

    public void execute(Runnable task) {
        if (isStopped) {
            throw new IllegalStateException("ThreadPool is stopped, cannot execute new tasks.");
        }

        tasks.offer(task);
    }

    public void shutdown() {
        isStopped = true;
        for (Thread thread : threads) {
            if (thread != null) {
                thread.interrupt();
            }
        }
    }

    public boolean isShutdown() {
        return isStopped;
    }
	
}
