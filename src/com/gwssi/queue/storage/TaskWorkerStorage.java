package com.gwssi.queue.storage;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.gwssi.queue.util.Config;

public class TaskWorkerStorage{
	public LinkedBlockingQueue<Runnable> threadCache = new LinkedBlockingQueue<Runnable>(Config.THREAD_WORKER);
	
	public void put(Runnable command) throws InterruptedException {
		synchronized (threadCache) {
			threadCache.offer(command, 2L, TimeUnit.SECONDS);
		}
	}

	public void poll() throws InterruptedException {
		synchronized (threadCache) {
			threadCache.poll(2L, TimeUnit.SECONDS);
		}
	}
}
