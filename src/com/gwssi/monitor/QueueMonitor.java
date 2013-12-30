package com.gwssi.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.gwssi.queue.api.QueueApi;
import com.gwssi.queue.thread.QueueThreadPoolExcutor;
import com.gwssi.queue.util.Config;

/**
 * 
 * 队列管理监控
 * 
 * @author NoGrief
 * 
 */
public class QueueMonitor {

	private static final Logger logger = Logger.getLogger(QueueMonitor.class);

	/**
	 * 缓存线程池状态
	 * 1、空闲可销毁
	 */
	private static Map<String, List<String>> statusMap = new HashMap<String, List<String>>();
	
	public static void startUpThreadExcutorMonitor() {
		logger.debug("Start up Thread Excutor Monitor");
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					Map<String, QueueThreadPoolExcutor> threadMap = QueueApi.getQueueThreadPoolExcutorMap();
					for (Entry<String, QueueThreadPoolExcutor> threadEntries : threadMap.entrySet()) {
						QueueThreadPoolExcutor excutor = threadEntries.getValue();
						checkExcutor(threadEntries.getKey(), excutor);
					}
					doCheck();
				}
			}
		}).start();
	}

	protected static void doCheck() {
		List<String> destoryList = statusMap.get("1");
		if (destoryList != null) {
			for (String typeId : destoryList) {
				QueueApi.removeQueueThreadPoolExcutor(typeId);
			}
			statusMap.remove("1");
		}
	}

	/**
	 * 检查线程池执行器状态 1、有任务执行 2、空闲 3、已关闭
	 * 
	 * @param excutor
	 * @return
	 */
	private static int checkExcutor(String typeId, QueueThreadPoolExcutor excutor) {
		int activeCount = excutor.getActiveCount();
		if (activeCount == 0) {
			long currentTime = System.currentTimeMillis();
			long excutorUpdateTime = excutor.getUpdateTime().getTime();
			long idleTime = currentTime - excutorUpdateTime;
			if (idleTime >= Config.THREAD_POOL_IDLE_TIME) {
				logger.debug("线程池执行器空闲超过" + Config.THREAD_POOL_IDLE_TIME + "毫秒，系统将关闭线程执行器！");
				excutor.shutdown();
				List<String> destoryTypeList = statusMap.get("1");
				if (destoryTypeList == null) {
					destoryTypeList = new ArrayList<String>();
					statusMap.put("1", destoryTypeList);
				}
				destoryTypeList.add(typeId);
				return 3;
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}
}
