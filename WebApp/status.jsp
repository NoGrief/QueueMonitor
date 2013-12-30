<%@page import="java.util.List"%>
<%@page import="com.gwssi.queue.thread.TaskThread"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="com.gwssi.monitor.ServerInformation"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.gwssi.queue.dispatcher.ThreadWorkerDispatcher"%>
<%@page import="com.gwssi.queue.storage.TaskInfoStorage"%>
<%@page import="java.util.Map"%>
<%@page import="com.gwssi.queue.api.QueueApi"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META HTTP-EQUIV="Refresh" CONTENT="1">
<title>Insert title here</title>

</head>
<body>
	<table width="100%" border="1" bordercolor="black">
		<tr>
			<td>JVM的CPU占用率：</td>
			<td><%=ServerInformation.cpuUse*100+"%" %></td>
		</tr>
		<tr>
			<td>JVM的Heap占用率：</td>
			<td><%=ServerInformation.getHeapMemery()*100+"%" %></td>
		</tr>
		<tr>
			<td>线程执行器数量：</td>
			<td><%=QueueApi.getQueueThreadPoolExcutorMap().size() %></td>
		</tr>
		<tr>
			<td>队列中的任务总数：</td>
			<td><%=QueueApi.getTotalTask()%>，其中准备运行的任务总数为：<%=QueueApi.getTotalPreparingTask() %>，正在运行的任务总数为：<%=QueueApi.getTotalRunningTask() %></td>
		</tr>
		<tr>
			<td>详细队列信息：</td>
			<td><%
				Map<String, TaskInfoStorage> tisMap = ThreadWorkerDispatcher.taskInfoStorageMap;
				for (Entry<String, TaskInfoStorage> tisEntry : tisMap.entrySet()) {
			%>
			任务类型ID：<%=tisEntry.getKey() %>，准备执行数量：<%=tisEntry.getValue().PREPARING_TASK.size() %>，正在执行数量：<%=tisEntry.getValue().RUNNING_TASK.size() %><br>
			当前Swap队列中任务数量：<%=tisEntry.getValue().SWAP_QUEUE.size()%><br>
			当前队列中任务总数量：<%=tisEntry.getValue().PREPARING_TASK_QUEUE.size()+tisEntry.getValue().RUNNING_TASK_QUEUE.size()%><br>
			当前准备队列中的任务数量：<%=tisEntry.getValue().PREPARING_TASK_QUEUE.size()%>，当前正在执行队列中的任务数量：<%=tisEntry.getValue().RUNNING_TASK_QUEUE.size()%><bR>
			已完成的任务：<br><%LinkedHashMap<String,TaskThread> finishedMap = tisEntry.getValue().FINISHED_TASK;
			for (Entry<String, TaskThread> finishedTask : finishedMap.entrySet()) {
			%>
				任务流水ID：<%=finishedTask.getKey() %>，任务对象：<%=finishedTask.getValue()+":"+finishedTask.getValue().getFlowId() %><br>
			<%
			}
			
			%><br>
			准备队列任务列表：<%
			List<TaskThread> prepareList = tisEntry.getValue().PREPARING_TASK_QUEUE;
			for(TaskThread t:prepareList){
			%>
				<%= t.getFlowId() %>
			<%
			}
			%><br>
			<hr>
			<%
			}
			%></td>
		</tr>
		<tr></tr>
		<tr></tr>
		<tr></tr>
		<tr></tr>
	</table>
</body>
</html>