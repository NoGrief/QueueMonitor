package com.gwssi.queue.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.gwssi.queue.dispatcher.ThreadWorkerDispatcher;
import com.gwssi.queue.model.ITaskObject;
import com.gwssi.queue.storage.TaskQueueStorage;
import com.gwssi.queue.util.Config;

public class TaskQueueManager {
	private static ExecutorService executorService = Executors.newFixedThreadPool(1);
	public static TaskQueueStorage taskQueueStorage = new TaskQueueStorage();
	public static ThreadWorkerDispatcher threadWorkerDispatcher = new ThreadWorkerDispatcher();
	public static boolean start = true;
	private static final Logger logger = Logger.getLogger(TaskQueueManager.class);

	public static void initTaskQueue() {
		logger.debug("初始化任务队列管理器");
		taskQueueStorage.addObserver(threadWorkerDispatcher);
	}

	public static void startTaskQueueGetter() {
		logger.debug("启动任务队列监控");
		executorService.execute(new Runnable() {
			public void run() {
				while (start) {
					try {
						int interval = Config.THREAD_POLL_TIME;
						if (interval < 200) {
							interval = 200;
						}
						Thread.sleep(Config.THREAD_POLL_TIME);
						synchronized (taskQueueStorage) {
							taskQueueStorage.pollTask();
						}
					} catch (InterruptedException e) {
						continue;
					}
				}
			}
		});
	}

	public static void putTask(ITaskObject task) {
		synchronized (taskQueueStorage) {
			taskQueueStorage.putTask(task);
		}
	}

	public static void stopGetter() {
		start = false;
	}

	public static void shutdown() {
		start = false;
		executorService.shutdown();
	}
}
