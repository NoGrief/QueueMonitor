package com.gwssi.queue.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.gwssi.monitor.ServerInformation;
import com.gwssi.queue.api.TaskQueueManager;

public class StartQueueManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public StartQueueManagerServlet() {
        super();
    }

	@Override
	public void init() throws ServletException {
		TaskQueueManager.initTaskQueue();
		TaskQueueManager.startTaskQueueGetter();
		//testCPU();
	}
	
	public void testCPU(){
		Thread testThread = new Thread(new Runnable() {
			
			public void run() {
				while(true){
					ServerInformation.getCpuPer();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				
			}
		});
		testThread.start();
	}

}
