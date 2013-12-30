package com.gwssi.monitor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gwssi.queue.api.QueueApi;

/**
 * 
 * 队列管理监控接口
 * 
 * @author nogrief
 * 
 */
public class QueueManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		if ("swap".equals(method)) {
			doSwap(request);
		}
	}

	//method=swap&opt=1|0|0&flowId=40281d8143418322014341834d20000b&typeId=S002-1288928
	private void doSwap(HttpServletRequest request) {
		String op = request.getParameter("opt");
		String typeId = request.getParameter("typeId");
		String flowId = request.getParameter("flowId");
		String[] opts = op.split("\\|");
		boolean up = opts[0].equals("1") ? true : false;
		boolean top = opts[1].equals("1") ? true : false;
		boolean bottom = opts[2].equals("1") ? true : false;
		QueueApi.SwapPrepareTask(typeId, flowId, up, top, bottom);
	}

}
