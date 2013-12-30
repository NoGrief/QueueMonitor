package com.gwssi.queue.dispatcher;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.gwssi.queue.api.QueueApi;
import com.gwssi.queue.storage.TaskInfoStorage;
import com.gwssi.queue.thread.QueueThreadPoolExcutor;
import com.gwssi.queue.thread.TaskThread;
import com.gwssi.queue.util.Config;

public class TaskWorkerDispatcher implements Observer {
	private static final Logger logger = Logger.getLogger(TaskWorkerDispatcher.class);

	private String typeId;
	
	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public synchronized void excuteRun() {
		synchronized (typeId) {
			TaskInfoStorage tis = QueueApi.getTaskInfoStorage(typeId);
			QueueThreadPoolExcutor executorService = QueueApi.getQueueThreadPoolExcutor(typeId);
			TaskThread task = null;
			if (tis.RUNNING_TASK.size() >= Config.THREAD_WORKER) {
				logger.info("任务执行线程已满，等待线程池内任务完成！");
				return;
			} else {
				task = tis.getPerparingTask();
				if (task == null) {
					logger.debug("未获取到新的任务。");
					return;
				}
				logger.debug("已经获取到新任务：" + task);
				tis.putRunningMap(task.getFlowId(), task);
			}
			logger.info("任务调度器正在执行任务！任务流水号：" + task.getFlowId());
			task.getTask().getTaskStatus().setOptStatus(3);
			executorService.execute(task);
			executorService.setUpdateTime(new Date());
		}

	}

	public synchronized void update(Observable o, Object arg) {
			logger.info("任务调度器被通知执行调度！任务类型："+this.typeId);
			excuteRun();
	}

}
