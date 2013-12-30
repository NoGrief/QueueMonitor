package com.gwssi.queue.model;

import com.gwssi.queue.api.IExcuteOperation;

public interface ITaskObject {
	public TaskStatus getTaskStatus();

	public void setTaskStatus(TaskStatus taskStatus);

	public void setFlowId(String id);

	public String getFlowId();

	public void setTypeId(String id);

	public String getTypeId();

	public void setEstimateTime(String timeStr);

	public String getEstimateTime();
	
	public void setExcutor(IExcuteOperation excutor);
	
	public IExcuteOperation getExcutor();
}
