package com.mayer.framework;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class LinkedBlockingThreadPool {
	
	private final BlockingQueue<Runnable> tasks;
	private final Thread[] threads;
	private final AtomicBoolean isStopped;

	public LinkedBlockingThreadPool(int maxThreads) {
		if (maxThreads <= 0) {
			throw new IllegalArgumentException("Max threads must be greater than 0");
		}

		tasks = new LinkedBlockingQueue<>();
		threads = new Thread[maxThreads];
		isStopped = new AtomicBoolean(false);

		for (int i = 0; i < maxThreads; i++) {
			createThread(i);
		}
	}

	private void createThread(int index) {
		threads[index] = new Thread(() -> {
			while (!isStopped.get() || !Thread.currentThread().isInterrupted()) {
				try {
					Runnable taskToExecute = tasks.take();
					taskToExecute.run();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});

		threads[index].start();
	}

	public void execute(Runnable task) {
		if (isStopped.get()) {
			throw new IllegalStateException("ThreadPool stopped. Cannot execute new tasks!");
		}

		try {
			tasks.put(task);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void shutdown() {
		isStopped.set(true);
		for (Thread thread : threads) {
			if (thread != null) {
				thread.interrupt();
			}
		}
	}

	public boolean isShutdown() {
		return isStopped.get();
	}
}