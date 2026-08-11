package service;

import java.util.Vector;

import model.view.ViewWorkBean;

public interface WorkService extends Service {
	Vector<ViewWorkBean> getWorkViews(String date);
	
	Vector<ViewWorkBean> getWorkViews(String id, String date);
}
