package com.gwssi.queue.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.gwssi.queue.dispatcher.ThreadWorkerDispatcher;
import com.gwssi.queue.model.ITaskObject;
import com.gwssi.queue.storage.TaskInfoStorage;
import com.gwssi.queue.storage.TaskWorkerStorage;
import com.gwssi.queue.thread.QueueThreadPoolExcutor;
import com.gwssi.queue.thread.TaskThread;

public class QueueApi {

	private static final Logger logger = Logger.getLogger(QueueApi.class);

	public static synchronized void putTask(final ITaskObject task) {
		logger.debug("-----Put新的任务-----");
		TaskQueueManager.putTask(task);
	}

	public static TaskInfoStorage getTaskInfoStorage(String type) {
		return ThreadWorkerDispatcher.taskInfoStorageMap.get(type);
	}

	public static TaskWorkerStorage getTaskQueueStorage(String type) {
		return ThreadWorkerDispatcher.taskWorkerStorageMap.get(type);
	}

	public static int getCurrentThreadLocation(String type, TaskThread taskThread) {
		return ThreadWorkerDispatcher.taskInfoStorageMap.get(type).getTaskRecordPosition(taskThread);
	}

	public static QueueThreadPoolExcutor getQueueThreadPoolExcutor(String type) {
		return ThreadWorkerDispatcher.threadPoolMap.get(type);
	}

	public static void removeQueueThreadPoolExcutor(final String type) {
		synchronized (type) {
			// 防止ConcurrentModificationException异常，先删除索引，在删除元素。
			Iterator<String> threadKeyIterator = ThreadWorkerDispatcher.threadPoolMap.keySet().iterator();
			while (threadKeyIterator.hasNext()) {
				String typeId = threadKeyIterator.next();
				if (typeId.equals(type)) {
					logger.debug("从缓存中移除线程执行器！");
					threadKeyIterator.remove();
					//ThreadWorkerDispatcher.threadPoolMap.remove(type);
				}
			}
		}
	}

	public static Map<String, QueueThreadPoolExcutor> getQueueThreadPoolExcutorMap() {
		return ThreadWorkerDispatcher.threadPoolMap;
	}

	public static Map<String, TaskInfoStorage> getTaskInfoStorageMap() {
		return ThreadWorkerDispatcher.taskInfoStorageMap;
	}

	public static long getTotalTask() {
		return getTotalPreparingTask() + getTotalRunningTask();
	}

	public static long getTotalPreparingTask() {
		long total = 0L;
		Map<String, TaskInfoStorage> tisMap = ThreadWorkerDispatcher.taskInfoStorageMap;
		for (Entry<String, TaskInfoStorage> tisEntry : tisMap.entrySet()) {
			TaskInfoStorage tis = tisEntry.getValue();
			total += tis.PREPARING_TASK.size();
		}
		return total;
	}

	public static long getTotalRunningTask() {
		long total = 0L;
		Map<String, TaskInfoStorage> tisMap = ThreadWorkerDispatcher.taskInfoStorageMap;
		for (Entry<String, TaskInfoStorage> tisEntry : tisMap.entrySet()) {
			TaskInfoStorage tis = tisEntry.getValue();
			total += tis.RUNNING_TASK.size();
		}
		return total;
	}

	public static synchronized int getTaskPosition(String typeId, String flowId) {
		TaskInfoStorage tis = getTaskInfoStorage(typeId);
		if (flowId == null) {
			return -1;
		}
		TaskThread tt = tis.PREPARING_TASK.get(flowId);
		int pos = -1;
		if (tt == null) {
			tt = tis.RUNNING_TASK.get(flowId);
		}
		if (tt != null) {
			pos = tis.getTaskQueuePosition(tt.getTask());
		}
		return pos;
	}

	public static List<String> getBeforeTasks(String typeId, int position) {
		TaskInfoStorage tis = getTaskInfoStorage(typeId);
		List<String> flowIdList = new ArrayList<String>();
		int size = tis.RUNNING_TASK_QUEUE.size();
		for (int i = 0; i < size - (size - position + 1); i++) {
			try {
				ITaskObject task = tis.PREPARING_TASK_QUEUE.get(i).getTask();
				if (task != null) {
					String flowid = task.getFlowId();
					flowIdList.add(flowid);
				}
			} catch (Exception e) {
				logger.debug(">>>>>>>>>>" + e);
				continue;
				// do nothing,maybe overflow
			}
		}
		return flowIdList;
	}

	public static void SwapPrepareTask(String typeId, String flowID, boolean up, boolean top, boolean bottom) {
		TaskInfoStorage tis = ThreadWorkerDispatcher.taskInfoStorageMap.get(typeId);
		if (tis != null) {
			LinkedHashMap<String, TaskThread> perparing = tis.PREPARING_TASK;
			for (Entry<String, TaskThread> taskThreadEntrys : perparing.entrySet()) {
				TaskThread tt = taskThreadEntrys.getValue();
				String flowId = tt.getFlowId();
				if (flowId.equals(flowID)) {
					synchronized (tis.PREPARING_TASK_QUEUE) {
						int currentPos = 0;
						if (top) {
							currentPos = tis.PREPARING_TASK_QUEUE.indexOf(tt);
							tis.PREPARING_TASK_QUEUE.remove(currentPos);
							tis.PREPARING_TASK_QUEUE.set(0, tt);
							return;
						}
						if (bottom) {
							currentPos = tis.PREPARING_TASK_QUEUE.indexOf(tt);
							tis.PREPARING_TASK_QUEUE.remove(currentPos);
							tis.PREPARING_TASK_QUEUE.set(tis.PREPARING_TASK_QUEUE.size() - 1, tt);
							return;
						}
						if (up) {
							if (currentPos > 0) {
								currentPos = tis.PREPARING_TASK_QUEUE.indexOf(tt);
								TaskThread temptt = tis.PREPARING_TASK_QUEUE.get(currentPos - 1);
								tis.PREPARING_TASK_QUEUE.set(currentPos - 1, tt);
								tis.PREPARING_TASK_QUEUE.set(currentPos, temptt);
								return;
							}
						} else if (!up) {
							if (currentPos != (tis.PREPARING_TASK_QUEUE.size() - 1)) {
								currentPos = tis.PREPARING_TASK_QUEUE.indexOf(tt);
								TaskThread temptt = tis.PREPARING_TASK_QUEUE.get(currentPos - 1);
								tis.PREPARING_TASK_QUEUE.set(currentPos + 1, tt);
								tis.PREPARING_TASK_QUEUE.set(currentPos, temptt);
								return;
							}
						}
						break;
					}

				}
			}
		}
	}
}
