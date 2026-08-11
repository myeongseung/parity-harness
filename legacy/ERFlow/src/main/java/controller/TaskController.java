package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.TaskBean;
import model.TaskHistoryBean;
import model.view.ViewTaskBean;
import service.implementation.TaskServiceImpl;


public class TaskController {
	private final TaskServiceImpl taskSvc;
	
	public TaskController() {
		taskSvc = new TaskServiceImpl();
	}
	
	public boolean createTask(HttpServletRequest request, Vector<TaskHistoryBean> products) {
		return taskSvc.createTask(request, products);
	}
	
	public boolean createTask(TaskBean task, Vector<TaskHistoryBean> products) {
		return taskSvc.createTask(task, products);
	}
	
	public boolean deleteTask(int taskId, int type) {
		return taskSvc.deleteTask(taskId, type);
	}
	
	public boolean updateTask(HttpServletRequest request) {
		return taskSvc.updateTask(request);
	}
	
	public boolean updateTask(TaskBean task) {
		return taskSvc.updateTask(task);
	}
	
	public TaskBean getTask(int taskId) {
		return taskSvc.getTask(taskId);
	}
	
	public Vector<ViewTaskBean> getTasks(String keyfield, String keyword , int start, int cnt, int type) {
		return taskSvc.getTasks(keyfield, keyword, start, cnt, type);
	}
	
	public int getTaskCount(String keyfield, String keyword, int type) {
		return taskSvc.getTaskCount(keyfield, keyword, type);
	}
	
	public boolean createTaskHistory(TaskHistoryBean bean) {
		return taskSvc.createTaskHistory(bean);
	};
	public boolean updateTaskHistory(TaskHistoryBean bean) {
		return taskSvc.updateTaskHistory(bean);
	};
	public boolean deleteTaskHistory(int taskId) {
		return taskSvc.deleteTaskHistory(taskId);
	};
	
	public Vector<TaskHistoryBean> getTaskHistories(String keyfield, String keyword , int start, int cnt) {
		return taskSvc.getTaskHistories(keyfield, keyword, start, cnt);
	};
	
	public int getTaskHistoriesCount(String keyfield, String keyword) {
		return taskSvc.getTaskHistoriesCount(keyfield, keyword);
	};
}
