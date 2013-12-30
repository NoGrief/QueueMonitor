package com.gwssi.queue.code;

public enum OperationStatusEnum {
	PREPARING(1, "准备执行"), BEFORE_RUN(2, "执行之前"), RUNNING(3, "执行中"), AFTER_RUN(4, "执行后"), FINISH(5, "执行完成"), SUSPEND(6, "执行暂停"), TERMINAL(7, "执行停止"), EXCEPTION(8, "执行错误");
	private String desciption;
	private int status;

	private OperationStatusEnum(int status, String desciption) {
		this.desciption = desciption;
		this.status = status;
	}

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
