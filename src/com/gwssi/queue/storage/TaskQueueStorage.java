package com.gwssi.queue.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.gwssi.queue.code.OperationStatusEnum;
import com.gwssi.queue.code.TaskStatusEnum;
import com.gwssi.queue.model.ITaskObject;

public class TaskQueueStorage extends Observable {
	private static final Logger logger = Logger.getLogger(TaskQueueStorage.class);

	public LinkedBlockingQueue<ITaskObject> taskQueue = new LinkedBlockingQueue<ITaskObject>();

	public void putTask(ITaskObject task) {
		synchronized (taskQueue) {
			try {
				logger.debug("任务被加入队列");
				taskQueue.put(task);
			} catch (InterruptedException e) {
				logger.error("将任务加入任务队列时出错！" + e);
			}
		}
	}

	public void pollTask() {
		synchronized (taskQueue) {
			ITaskObject task = taskQueue.poll();
			if (task == null) {
				return;
			}
			task.getTaskStatus().setTaskStatus(TaskStatusEnum.NON_STRART.getValue());
			task.getTaskStatus().setOptStatus(OperationStatusEnum.PREPARING.getStatus());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("task", task);
			sendTask(params);
		}
	}

	public void sendTask(Object params) {
		synchronized (params) {
			setChanged();
			notifyObservers(params);
		}
	}
}
