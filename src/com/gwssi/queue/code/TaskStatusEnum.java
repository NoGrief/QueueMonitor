package com.gwssi.queue.code;

public enum TaskStatusEnum {
	NON_STRART(1, "未开始"),RUNNING(2, "运行中"),  FINISH(2, "已完成");
	private String name;
	private int value;

	private TaskStatusEnum(int value, String name) {
		this.name = name;
		this.value = value;
	}

	public int status(String name) {
		return this.value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
