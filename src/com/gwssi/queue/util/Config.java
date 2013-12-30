package com.gwssi.queue.util;

public class Config {
	//工作线程数，默认是1个
	public static int THREAD_WORKER = 1;
	//队列轮询时间，默认是200毫秒，且最小为200
	public static int THREAD_POLL_TIME = 200;
	//CPU估算系数（倍数）
	public static int CPU_ESTIMATE_RATIO = 3;
	//是否缓存已经完成的任务
	public static boolean CACHE_FINISHED = false;
}
