package com.gwssi.queue.thread;

import org.apache.log4j.Logger;

import com.gwssi.queue.api.IExcuteOperation;
import com.gwssi.queue.dispatcher.TaskWorkerDispatcher;
import com.gwssi.queue.exception.QueueBizRuntimeException;
import com.gwssi.queue.model.ITaskObject;

public class TaskThread implements Runnable {
	private static final Logger logger = Logger.getLogger(TaskWorkerDispatcher.class);
	private ITaskObject task;
	private IExcuteOperation excuter;
	private String flowId;
	
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public ITaskObject getTask() {
		return task;
	}

	public IExcuteOperation getExcuter() {
		return excuter;
	}

	public void setTask(ITaskObject task) {
		this.task = task;
	}

	public void setExcuter(IExcuteOperation excuter) {
		this.excuter = excuter;
	}

	public TaskThread(ITaskObject task,IExcuteOperation excuter){
		this.setExcuter(excuter);
		this.setTask(task);
	}
	
	public void run() {
		try {
			task.getTaskStatus().setOptStatus(2);
			logger.debug("-----执行队列中的任务-----");
			excuter.running();
		} catch (QueueBizRuntimeException e) {
			try {
				task.getTaskStatus().setOptStatus(8);
				excuter.exception();
			} catch (QueueBizRuntimeException e1) {
				logger.error(e);
				logger.debug("执行任务中出错，操作状态改为异常！");
				throw new RuntimeException(e1);
			}
			logger.error(e);
			logger.debug("执行任务中出错，操作状态改为异常！");
			throw new RuntimeException(e);
		}
	}

}
