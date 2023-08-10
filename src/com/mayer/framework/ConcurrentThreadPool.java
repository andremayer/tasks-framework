package com.mayer.framework;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/** Ref:
	https://www.baeldung.com/java-concurrent-queues#7-concurrentlinkedqueue
	https://www.baeldung.com/java-queue-linkedblocking-concurrentlinked#concurrentlinkedqueue
*/
@State(Scope.Benchmark)
public class ConcurrentThreadPool {

	private ConcurrentLinkedQueue<Runnable> tasks;
    private Thread[] threads;
    private boolean isStopped;
    
    public ConcurrentThreadPool () {
    	tasks = new ConcurrentLinkedQueue<>();
        threads = new Thread[3];
        isStopped = false;

        for (int i = 0; i < 3; i++) {
            createThread(i);
        }
    }

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
	
    
	@Benchmark
	public void execute(ConcurrentThreadPool threadPool) {
		for (int i = 0; i < 5; i++) {
            Task task = new Task("Task nº: " + i);
            threadPool.execute(task);
        }
	}
    
}
