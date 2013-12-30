package com.gwssi.monitor.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.gwssi.queue.api.MonitorApi;

/**
 * Servlet implementation class QueueMonitorServlet
 */
public class QueueMonitorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		MonitorApi.initMonitor();
	}

}
