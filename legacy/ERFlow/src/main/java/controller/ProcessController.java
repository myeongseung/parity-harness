package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.ProcessBean;
import model.ProposalBean;
import model.view.ViewProposalBean;
import service.implementation.ProcessServiceImpl;
import service.implementation.ProposalServiceImpl;

public class ProcessController {
	private final ProcessServiceImpl processSvc;
	
	public ProcessController() {
		processSvc = new ProcessServiceImpl();
	}
	
	public boolean createProcess(HttpServletRequest request) {
		return processSvc.createProcess(request);
	};
	public boolean createProcess(ProcessBean process) {
		return processSvc.createProcess(process);
	};
	public boolean deleteProcess(String processId) {
		return processSvc.deleteProcess(processId);
	};
	public boolean updateProcess(HttpServletRequest request) {
		return processSvc.updateProcess(request);
	};
	public boolean updateProcess(ProcessBean process) {
		return processSvc.updateProcess(process);
	};
	
	public ProcessBean getProcess(String processId) {
		return processSvc.getProcess(processId);
	};
	
	public Vector<ProcessBean> getProcesses(String keyfield, String keyword, int start, int cnt) {
		return processSvc.getProcesses(keyfield, keyword, start, cnt);
	};
	
	public int processesCount(String keyfield, String keyword) {
		return processSvc.processesCount(keyfield, keyword);
	};
}
