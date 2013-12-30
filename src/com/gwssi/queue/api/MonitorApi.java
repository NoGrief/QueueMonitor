package com.gwssi.queue.api;

import org.apache.log4j.Logger;

import com.gwssi.monitor.QueueMonitor;

public class MonitorApi {

	private static final Logger logger = Logger.getLogger(MonitorApi.class);

	public static void initMonitor(){
		logger.debug("启动线程池执行器监视器！");
		QueueMonitor.startUpThreadExcutorMonitor();
	}



}
