package com.gwssi.queue.api;

import com.gwssi.queue.exception.QueueBizRuntimeException;
import com.gwssi.queue.model.ITaskObject;

//PREPARING(1, "准备执行"), BEFORE_RUN(2, "执行之前"), RUNNING(3, "执行中"), AFTER_RUN(4, "执行后"), FINISH(5, "执行完成"), SUSPEND(6, "执行暂停"), TERMINAL(7, "执行停止"), EXCEPTION(8, "执行错误");

public interface IExcuteOperation {

	public ITaskObject getTaskObject();

	public void setTaskObject(ITaskObject taskObject);

	public void perparing() throws QueueBizRuntimeException;

	public void beforeRun() throws QueueBizRuntimeException;

	public void running() throws QueueBizRuntimeException;

	public void afterRun() throws QueueBizRuntimeException;

	public void finish() throws QueueBizRuntimeException;

	public void supend() throws QueueBizRuntimeException;

	public void terminal() throws QueueBizRuntimeException;

	public void exception() throws QueueBizRuntimeException;
	

	/**
	 * 更新任务信息，可能包括任务所在队列位置，耗时更新，此接口可能与业务相关
	 * 当任务在队列中发生位置变化时，将触发此接口
	 */
	public void updateTaskInfo();
}
