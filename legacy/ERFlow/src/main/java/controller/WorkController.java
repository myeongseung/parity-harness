package controller;

import java.util.Vector;

import model.view.ViewWorkBean;
import service.implementation.WorkServiceImpl;

public class WorkController {
	private final WorkServiceImpl workSvc;
	
	public WorkController() {
		workSvc = new WorkServiceImpl();
	}
	
	public Vector<ViewWorkBean> getWorkViews(String date) {
		return workSvc.getWorkViews(date);
	}
	
	public Vector<ViewWorkBean> getWorkViews(String id, String date) {
		return workSvc.getWorkViews(id, date);
	}
}
