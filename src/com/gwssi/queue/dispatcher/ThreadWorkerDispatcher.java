package com.gwssi.queue.dispatcher;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.gwssi.queue.model.ITaskObject;
import com.gwssi.queue.storage.TaskInfoStorage;
import com.gwssi.queue.storage.TaskWorkerStorage;
import com.gwssi.queue.thread.QueueThreadPoolExcutor;
import com.gwssi.queue.util.Config;

public class ThreadWorkerDispatcher extends Observable implements Observer {
	private static final Logger logger = Logger.getLogger(ThreadWorkerDispatcher.class);
	public static Map<String, TaskInfoStorage> taskInfoStorageMap = new HashMap<String, TaskInfoStorage>();
	public static Map<String, TaskWorkerStorage> taskWorkerStorageMap = new HashMap<String, TaskWorkerStorage>();
	public static Map<String, QueueThreadPoolExcutor> threadPoolMap = new HashMap<String, QueueThreadPoolExcutor>();
	public static Map<String, PolicyWorkerDispatcher> policyWorkerDispatcherMap = new HashMap<String, PolicyWorkerDispatcher>();
	public static Map<String , TaskWorkerDispatcher> taskWorkerDispatcherMap = new HashMap<String, TaskWorkerDispatcher>();

	public void update(Observable o, Object arg) {
		logger.debug("-----线程调度器正在为任务分配线程-----");
		@SuppressWarnings("unchecked")
		Map<String, Object> params = (Map<String, Object>)arg;
		ITaskObject pTask = (ITaskObject)params.get("task");
		String typeId = pTask.getTypeId();
		if (typeId==null || "".equals(typeId)) {
			logger.debug("无法从任务对象中（ITaskObjcet）获取任务类型ID，此任务将被加入default队列。");
			typeId = "default";
			pTask.setTypeId(typeId);
		}else{
			TaskInfoStorage taskInfo = taskInfoStorageMap.get(typeId);
			if (taskInfo==null) {
				logger.debug("创建新的任务信息缓存");
				taskInfo = new TaskInfoStorage();
				taskInfoStorageMap.put(typeId, taskInfo);
			}
			logger.info("将任务放入任务信息队列。");
			taskInfo.SWAP_QUEUE.push(pTask);
			TaskWorkerStorage taskWorker = taskWorkerStorageMap.get(typeId);
			if (taskWorker==null) {
				logger.debug("创建新的任务工作缓存");
				taskWorker = new TaskWorkerStorage();
				taskWorkerStorageMap.put(typeId, taskWorker);
			}
			
			QueueThreadPoolExcutor qtpe = threadPoolMap.get(typeId);
			if (qtpe == null) {
				logger.debug("创建新的任务执行环境");
				qtpe = new QueueThreadPoolExcutor(Config.THREAD_WORKER, Config.THREAD_WORKER, 0L, TimeUnit.SECONDS, taskWorker.threadCache);
				qtpe.setTypeId(typeId);
				qtpe.setCreateTime(new Date());
				qtpe.setUpdateTime(new Date());
				threadPoolMap.put(typeId, qtpe);
			}
			
			PolicyWorkerDispatcher policyWorkerDp = policyWorkerDispatcherMap.get(typeId);
			if (policyWorkerDp == null) {
				logger.debug("创建新的任务策略调度器");
				policyWorkerDp = new PolicyWorkerDispatcher();
				policyWorkerDp.setTypeId(typeId);
				policyWorkerDispatcherMap.put(typeId, policyWorkerDp);
				this.addObserver(policyWorkerDp);
			}
			
			TaskWorkerDispatcher taskWorkerDispatcher = taskWorkerDispatcherMap.get(typeId);
			if (taskWorkerDispatcher==null) {
				logger.debug("创建任务执行调度器");
				taskWorkerDispatcher = new TaskWorkerDispatcher();
				taskWorkerDispatcher.setTypeId(typeId);
				taskWorkerDispatcherMap.put(typeId, taskWorkerDispatcher);
			}
			taskInfo.addObserver(policyWorkerDp);
			taskInfo.addObserver(taskWorkerDispatcher);
			policyWorkerDp.addObserver(taskWorkerDispatcher);
		}
		sendTask(pTask.getExcutor());
	}
	
	public void sendTask(Object params){
		this.setChanged();
		this.notifyObservers(params);
	}
}
