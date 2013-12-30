package com.gwssi.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import com.gwssi.queue.util.Config;

public class ServerInformation {
	private static final Logger logger = Logger.getLogger(ServerInformation.class);

	public static double cpuUse = 0;

	public static long getJVMTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public static long getJVMFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	public static long getJVMMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	public static long getJVMHeapMemoryUsageUsed() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
	}

	public static long getJVMHeapMemoryUsageMax() {
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
	}

	public static long getJVMNonHeapMemoryUsage() {
		return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
	}

	// 获取jvm heap内存使用率
	public static double getHeapMemery() throws InterruptedException {
		long totalMem = ServerInformation.getJVMTotalMemory();
		long usedMem = ServerInformation.getJVMHeapMemoryUsageUsed();

		double heapUtilization = (Double) (usedMem * 1.0 / totalMem * 1.0);

		return heapUtilization;
	}

	// 获取jvm heap内存未用率
	public static double getNoHeapMemery() throws InterruptedException {
		return 1 - getHeapMemery();
	}

	class RunTest implements Runnable {

		public boolean flag = true;

		public void run() {
			while (flag) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new RuntimeException("CPU测试线程出现异常。" + e);
				}
			}
		}

		public void stop() {
			this.flag = false;
		}

	}

	// 获取jvm cpu占用率
	public static void getCpuPer() {
		Thread testCpu = new Thread(new Runnable() {

			public void run() {
				ServerInformation si = new ServerInformation();
				RunTest run = si.new RunTest();
				Thread thread = new Thread(run);

				RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
				OperatingSystemMXBean opMxBean = ManagementFactory.getOperatingSystemMXBean();

				long startUpTime = runtimeBean.getUptime();
				long StartProcessCpuTime = ((com.sun.management.OperatingSystemMXBean) opMxBean).getProcessCpuTime();
				thread.start();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				long endUpTime = runtimeBean.getUptime();
				run.stop();
				long endProcessCpuTime = ((com.sun.management.OperatingSystemMXBean) opMxBean).getProcessCpuTime();
				double CPUutilization = (endProcessCpuTime - StartProcessCpuTime) / 1000000.0 / (endUpTime - startUpTime) / opMxBean.getAvailableProcessors();
				cpuUse = CPUutilization * Config.CPU_ESTIMATE_RATIO > 1 ? 1 : CPUutilization * Config.CPU_ESTIMATE_RATIO;
			}
		});
		testCpu.start();
	}

	// 获取jvm cpu未用率
	// public static double getNoCpuPer() throws InterruptedException {
	// return 1 - getCpuPer();
	// }

	public static void main(String[] args) throws InterruptedException {
		logger.debug(ServerInformation.getJVMFreeMemory());
		logger.debug(ServerInformation.getJVMMaxMemory());
		logger.debug(ServerInformation.getJVMTotalMemory());
		logger.debug(ServerInformation.getJVMHeapMemoryUsageUsed());
		logger.debug(ServerInformation.getJVMHeapMemoryUsageMax());
		logger.debug(ServerInformation.getJVMNonHeapMemoryUsage());

		logger.debug(new DecimalFormat("#.##%").format(ServerInformation.getHeapMemery()) + "==========");
		// logger.debug(new
		// DecimalFormat("#.##%").format(ServerInformation.getCpuPer()) +
		// "==========");
		//
		// logger.debug((ServerInformation.getHeapMemery() * 0.5) +
		// (ServerInformation.getCpuPer()));
	}
}
