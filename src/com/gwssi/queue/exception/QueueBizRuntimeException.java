package com.gwssi.queue.exception;

public class QueueBizRuntimeException extends Exception {
	private static final long serialVersionUID = 810739004314312682L;

	public QueueBizRuntimeException(String msg, Throwable e) {
		super(msg, e);
	}
}
