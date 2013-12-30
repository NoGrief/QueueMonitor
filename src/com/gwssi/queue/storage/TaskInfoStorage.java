package com.gwssi.queue.storage;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Observable;

import org.apache.log4j.Logger;

import com.gwssi.queue.model.ITaskObject;
import com.gwssi.queue.thread.TaskThread;

/**
 * @author 景龙
 * 
 */
public class TaskInfoStorage extends Observable {
	public LinkedHashMap<String, TaskThread> RUNNING_TASK = new LinkedHashMap<String, TaskThread>();
	public LinkedHashMap<String, TaskThread> PREPARING_TASK = new LinkedHashMap<String, TaskThread>();
	public LinkedHashMap<String, TaskThread> FINISHED_TASK = new LinkedHashMap<String, TaskThread>();
	public LinkedList<ITaskObject> SWAP_QUEUE = new LinkedList<ITaskObject>();
	public LinkedList<TaskThread> RUNNING_TASK_QUEUE = new LinkedList<TaskThread>();
	public LinkedList<TaskThread> PREPARING_TASK_QUEUE = new LinkedList<TaskThread>();
	private static final Logger logger = Logger.getLogger(TaskInfoStorage.class);

	/**
	 * 
	 * 加入准备队列，并缓存任务
	 * 
	 * @param flowId
	 * @param taskThread
	 */
	public void putPerparingMap(String flowId, TaskThread taskThread) {
		synchronized (PREPARING_TASK) {
			PREPARING_TASK.put(flowId, taskThread);
		}
		addToPreparingTaskQueue(taskThread);
	}

	/**
	 * 任务开始执行后，从准备缓存中移除
	 * 
	 * @param flowId
	 */
	public synchronized void removePerparingMap(String flowId) {
		synchronized (PREPARING_TASK) {
			PREPARING_TASK.remove(flowId);
		}
	}

	/**
	 * 
	 * 缓存完成的任务
	 * 
	 * @param flowId
	 * @param taskThread
	 */
	public void putFinishedMap(String flowId, TaskThread taskThread) {
		synchronized (FINISHED_TASK) {
			FINISHED_TASK.put(flowId, taskThread);
		}
	}

	/**
	 * 
	 * 从完成缓存中移除
	 * 
	 * @param flowId
	 */
	public synchronized void removeFinishedMap(String flowId) {
		synchronized (FINISHED_TASK) {
			FINISHED_TASK.remove(flowId);
		}
	}

	/**
	 * 
	 * 从准备列表中获取任务
	 * 
	 * @return
	 */
	public TaskThread getPerparingTask() {
		TaskThread task = null;
		synchronized (PREPARING_TASK_QUEUE) {
			task = PREPARING_TASK_QUEUE.poll();
		}
		if(task != null){
			this.removePerparingMap(task.getFlowId());
		}
		return task;
	}

	/**
	 * 加入到正在执行队列，并缓存
	 * 
	 * @param flowId
	 * @param taskThread
	 */
	public void putRunningMap(String flowId, TaskThread taskThread) {
		logger.debug("将任务线程加入到正在执行队列中！" + "flowId" + flowId);
		synchronized (RUNNING_TASK) {
			RUNNING_TASK.put(flowId, taskThread);
		}
		synchronized (RUNNING_TASK_QUEUE) {
			RUNNING_TASK_QUEUE.push(taskThread);
		}
	}

	/**
	 * 
	 * 从执行队列中移除，任务完成后调用此方法
	 * @param flowId
	 */
	public synchronized void removeRunningMap(String flowId) {
		logger.debug("将任务线程移除正在执行队列中！" + "flowId" + flowId);
		synchronized (RUNNING_TASK) {
			RUNNING_TASK.remove(flowId);
		}
	}

	public int getTaskRecordPosition(TaskThread taskThread) {
		ITaskObject task = taskThread.getTask();
		int index = -1;
		synchronized (SWAP_QUEUE) {
			index = SWAP_QUEUE.indexOf(task);
		}
		runTask();
		return index;
	}

	public void addToPreparingTaskQueue(TaskThread task) {
		synchronized (PREPARING_TASK_QUEUE) {
			PREPARING_TASK_QUEUE.push(task);
		}
		runTask();
	}

	public void removeTaskFromQueue(TaskThread task) {
		synchronized (RUNNING_TASK_QUEUE) {
			RUNNING_TASK_QUEUE.remove(task);
		}
		runTask();
	}

	public int getTaskQueuePosition(ITaskObject task) {
		int index = -1;
		synchronized (RUNNING_TASK_QUEUE) {
			index = RUNNING_TASK_QUEUE.indexOf(task);
		}
		if (index == -1) {
			index = PREPARING_TASK_QUEUE.indexOf(task) + RUNNING_TASK_QUEUE.size();
		}
		return index;
	}

	public void runTask() {
		logger.debug("通知策略调度器执行更新任务位置操作！");
		this.setChanged();
		this.notifyObservers();
	}
}
