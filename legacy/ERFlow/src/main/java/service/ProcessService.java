package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.ProcessBean;

public interface ProcessService extends Service {
	boolean createProcess(HttpServletRequest request);
	boolean createProcess(ProcessBean process);
	boolean deleteProcess(String processId);
	boolean updateProcess(HttpServletRequest request);
	boolean updateProcess(ProcessBean process);
	
	ProcessBean getProcess(String processId);
	
	Vector<ProcessBean> getProcesses(String keyfield, String keyword, int start, int cnt);
	
	int processesCount(String keyfield, String keyword);
}
