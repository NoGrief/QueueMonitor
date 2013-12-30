package com.gwssi.queue.thread;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.gwssi.queue.api.IExcuteOperation;
import com.gwssi.queue.api.QueueApi;
import com.gwssi.queue.exception.QueueBizRuntimeException;
import com.gwssi.queue.storage.TaskInfoStorage;
import com.gwssi.queue.util.Config;

public class QueueThreadPoolExcutor extends ThreadPoolExecutor {
	private static final Logger logger = Logger.getLogger(QueueThreadPoolExcutor.class);

	private String typeId;
	
	private Date createTime;
	
	private Date updateTime;

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public QueueThreadPoolExcutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public QueueThreadPoolExcutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public QueueThreadPoolExcutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	public QueueThreadPoolExcutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		TaskThread task = (TaskThread) r;
		IExcuteOperation excutor = task.getExcuter();
		if (excutor != null) {
			try {
				logger.debug("-----执行beforeRun------");
				task.getTask().getTaskStatus().setOptStatus(2);
				excutor.beforeRun();
			} catch (QueueBizRuntimeException e) {
				try {
					task.getTask().getTaskStatus().setOptStatus(8);
					logger.error(e);
					excutor.exception();
				} catch (QueueBizRuntimeException e1) {
					task.getTask().getTaskStatus().setOptStatus(8);
					logger.error(e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		TaskThread task = (TaskThread) r;
		IExcuteOperation excutor = task.getExcuter();
		TaskInfoStorage tis = QueueApi.getTaskInfoStorage(this.getTypeId());
		if (excutor != null) {
			try {
				logger.debug("-----执行afterRun------");
				task.getTask().getTaskStatus().setOptStatus(4);
				excutor.afterRun();
			} catch (Exception e) {
				try {
					logger.error(e);
					excutor.exception();
				} catch (QueueBizRuntimeException e1) {
					logger.error(e);
					throw new RuntimeException(e);
				}
				throw new RuntimeException(e);
			}

			try {
				logger.debug("------执行任务finish操作-----");
				task.getTask().getTaskStatus().setOptStatus(5);
				excutor.finish();
				tis.removeRunningMap(task.getFlowId());
				tis.removeTaskFromQueue(task);
				if (Config.CACHE_FINISHED) {
					tis.putFinishedMap(task.getFlowId(), task);
				}
			} catch (QueueBizRuntimeException e) {
				try {
					logger.error(e);
					excutor.exception();
				} catch (QueueBizRuntimeException e1) {
					logger.error(e);
					throw new RuntimeException(e);
				}
				throw new RuntimeException(e);
			}
			this.setUpdateTime(new Date());
		}
	}

	@Override
	protected void terminated() {
		logger.debug("-----执行线程池已停止！------");
	}
}
