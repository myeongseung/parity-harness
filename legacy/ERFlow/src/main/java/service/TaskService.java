package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.TaskBean;
import model.TaskHistoryBean;
import model.view.ViewTaskBean;

public interface TaskService extends Service {
	boolean createTask(HttpServletRequest request, Vector<TaskHistoryBean> products);
	boolean createTask(TaskBean task, Vector<TaskHistoryBean> products);
	boolean deleteTask(int taskId, int type);
	boolean updateTask(HttpServletRequest request);
	boolean updateTask(TaskBean task);
	
	boolean createTaskHistory(TaskHistoryBean bean);
	boolean updateTaskHistory(TaskHistoryBean bean);
	boolean deleteTaskHistory(int taskId);
	
	TaskBean getTask(int taskId);
	
	Vector<ViewTaskBean> getTasks(String keyfield, String keyword , int start, int cnt, int type);
	
	int getTaskCount(String keyfield, String keyword, int type);
	
	Vector<TaskHistoryBean> getTaskHistories(String keyfield, String keyword , int start, int cnt);
	
	int getTaskHistoriesCount(String keyfield, String keyword);
}
