package com.gwssi.queue.dispatcher;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.gwssi.queue.api.IExcuteOperation;
import com.gwssi.queue.api.QueueApi;
import com.gwssi.queue.exception.QueueBizRuntimeException;
import com.gwssi.queue.model.ITaskObject;
import com.gwssi.queue.storage.TaskInfoStorage;
import com.gwssi.queue.thread.TaskThread;
import com.gwssi.queue.util.UUIDGenerator;

public class PolicyWorkerDispatcher extends Observable implements Observer {
	private static final Logger logger = Logger.getLogger(PolicyWorkerDispatcher.class);
	private IExcuteOperation excutor;
	private String typeId;

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public void update(Observable o, Object arg) {
		if (o instanceof ThreadWorkerDispatcher) {
			logger.debug("收到线程管理器通知，执行任务策略！");
			this.taskOperation(arg);
		}else if(o instanceof TaskInfoStorage){
			updateTaskInfo();
		}
	}

	private void updateTaskInfo() {
		excutor.updateTaskInfo();
	}

	private synchronized void taskOperation(Object arg) {
		this.excutor = (IExcuteOperation) arg;
		ITaskObject task = ThreadWorkerDispatcher.taskInfoStorageMap.get(this.typeId).SWAP_QUEUE.poll();
		if (task == null) {
			return;
		} else {
			prepareTask(task);
		}
		this.excutor = (IExcuteOperation) arg;
		task.getTaskStatus().setOptStatus(1);
		logger.debug("执行任务策略");
		if (!doPolicy(task)) {
			return;
		} else {
			sendTask();
		}
	}

	private void prepareTask(ITaskObject task) {
		UUIDGenerator uuidGenerator = new UUIDGenerator();
		String flowId = uuidGenerator.generate().toString();
		TaskThread taskThread = new TaskThread(task, excutor);
		taskThread.setFlowId(flowId);
		taskThread.getTask().setFlowId(flowId);
		TaskInfoStorage tis = QueueApi.getTaskInfoStorage(typeId);
		tis.putPerparingMap(flowId, taskThread);
		excutor.setTaskObject(task);
		try {
			excutor.perparing();
		} catch (QueueBizRuntimeException e) {
			logger.error("用户API添加任务时出错，有可能是执行IExcuteOperation的实现中perparing方法出错！");
			throw new RuntimeException(e);
		}
	}

	private boolean doPolicy(ITaskObject task) {
		return true;
	}

	public IExcuteOperation getExcutor() {
		return excutor;
	}

	public void setExcutor(IExcuteOperation excutor) {
		this.excutor = excutor;
	}

	public synchronized void sendTask() {
		logger.debug("发送任务通知到任务调度器！");
		this.setChanged();
		this.notifyObservers();
	}

}
