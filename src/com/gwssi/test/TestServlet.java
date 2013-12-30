package com.gwssi.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.gwssi.queue.api.IExcuteOperation;
import com.gwssi.queue.api.QueueApi;
import com.gwssi.queue.exception.QueueBizRuntimeException;
import com.gwssi.queue.model.ITaskObject;
import com.gwssi.queue.model.TaskStatus;

/**
 * Servlet implementation class TestServlet
 */
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	class TestTaskObject implements ITaskObject {
		private TaskStatus taskStatus;
		private String flowId;
		private String typeId;
		private String estimateTime;
		private IExcuteOperation excutor;

		public void setTaskStatus(TaskStatus taskStatus) {
			this.taskStatus = taskStatus;

		}

		public TaskStatus getTaskStatus() {
			return this.taskStatus == null ? new TaskStatus() : this.taskStatus;
		}

		public void setFlowId(String id) {
			this.flowId = id;
		}

		public String getFlowId() {
			return this.flowId;
		}

		public void setTypeId(String typeId) {
			this.typeId = typeId;
		}

		public String getTypeId() {
			return this.typeId;
		}

		public void setEstimateTime(String estimateTime) {
			this.estimateTime = estimateTime;
		}

		public String getEstimateTime() {
			return estimateTime;
		}

		@Override
		public void setExcutor(IExcuteOperation excuter) {
			this.excutor = excuter;
		}

		@Override
		public IExcuteOperation getExcutor() {
			return this.excutor;
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		IExcuteOperation excuter = new IExcuteOperation() {
			private final Logger logger = Logger.getLogger(IExcuteOperation.class);
			private ITaskObject taskObject;

			public void terminal() throws QueueBizRuntimeException {
				logger.debug("Excute Terminal");
			}

			public void supend() throws QueueBizRuntimeException {
				logger.debug("Excute supend");
			}

			public void running() throws QueueBizRuntimeException {
				logger.debug("Excute running");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new QueueBizRuntimeException("", e);
				}
			}

			public void perparing() throws QueueBizRuntimeException {
				logger.debug("Excute perparing");
			}

			public void finish() throws QueueBizRuntimeException {
				logger.debug("Excute finish");
			}

			public void exception() throws QueueBizRuntimeException {
				logger.debug("Excute exception");
			}

			public void beforeRun() throws QueueBizRuntimeException {
				logger.debug("Excute beforeRun");
			}

			public void afterRun() throws QueueBizRuntimeException {
				logger.debug("Excute afterRun");
			}

			public ITaskObject getTaskObject() {
				return taskObject;
			}

			public void setTaskObject(ITaskObject taskObject) {
				this.taskObject = taskObject;
			}

			public void updateTaskInfo() {
				logger.debug("updateTaskInfo");
			}
		};
		String[] taskType = { "S001-1188291", "S002-1288928", "S003-12929818" };

		for (String typeId : taskType) {
			for (int i = 0; i < 10; i++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				TestTaskObject task = new TestTaskObject();
				task.setExcutor(excuter);
				task.setTypeId(typeId);
				QueueApi.putTask(task);
			}
		}

		// for (int i = 0; i < 2; i++) {
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// String UUID = new UUIDGenerator().generate().toString();
		// for (int j = 0; j < 10; j++) {
		// try {
		// Thread.sleep(100);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// //"40285c8141bb0ace0141bb1061f300a8"
		// task.setTypeId(UUID);
		// QueueApi.putTask(task);
		// }
		// }
	}

}
