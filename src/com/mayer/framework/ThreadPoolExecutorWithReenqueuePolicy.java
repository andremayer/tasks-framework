package com.mayer.framework;

import java.util.concurrent.*;

class ThreadPoolExecutorWithReenqueuePolicy {
	
	private ThreadPoolExecutor executor;

    private class ReenqueueRejectedHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RejectedExecutionException("Task re-enqueue interrupted", e);
                }
            }
        }
    }

    public ThreadPoolExecutorWithReenqueuePolicy(int corePoolSize, int maxPoolSize, long keepAliveTime, int queueSize) {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
        executor.setRejectedExecutionHandler(new ReenqueueRejectedHandler());
    }

    public void execute(Runnable task) {
        executor.execute(task);
    }

    public void shutdown() {
        executor.shutdown();
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }
}
