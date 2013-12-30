package com.gwssi.queue.model;

import java.io.Serializable;

public class TaskStatus implements Serializable {
	private static final long serialVersionUID = 6912125990090237514L;
	private int optStatus;
	private int taskStatus;

	public int getOptStatus() {
		return optStatus;
	}

	public void setOptStatus(int optStatus) {
		this.optStatus = optStatus;
	}

	public int getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}

}
