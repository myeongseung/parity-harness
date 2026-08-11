package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.BoundBean;
import model.view.ViewBoundBean;
import service.implementation.BoundServiceImpl;

public class BoundController {
	private final BoundServiceImpl boundSvc;
	
	public BoundController() {
		boundSvc = new BoundServiceImpl();
	}
	
	public boolean createBound(HttpServletRequest request, int type) {
		boolean flag = false;
		if (type == 0) {
			flag =  boundSvc.createInbound(request);
		} else if (type == 1) {
			flag =  boundSvc.createOutbound(request);
		}
		return flag;
	}
	
	public boolean createBound(BoundBean bean, int type) {
		boolean flag = false;
		if (type == 0) {
			flag =  boundSvc.createInbound(bean);
		} else if (type == 1) {
			flag =  boundSvc.createOutbound(bean);
		}
		return flag;
	}
	
	public boolean deleteBound(int boundId, int type) {
		boolean flag = false;
		if (type == 0) {
			flag =  boundSvc.deleteInbound(boundId);
		} else if (type == 1) {
			flag =  boundSvc.deleteOutbound(boundId);
		}
		return flag;
	}
	
	public boolean updateBound(HttpServletRequest request, int type) {
		boolean flag = false;
		if (type == 0) {
			flag =  boundSvc.updateInbound(request);
		} else if (type == 1) {
			flag =  boundSvc.updateOutbound(request);
		}
		return flag;
	}
	
	public boolean updateBound(BoundBean bound , int type) {
		boolean flag = false;
		if (type == 0) {
			flag =  boundSvc.updateInbound(bound);
		} else if (type == 1) {
			flag =  boundSvc.updateOutbound(bound);
		}
		return flag;
	}
	
	public int getBoundCount(String keyfield, String keyword, int type) {
		int cnt = 0;
		if (type == 0) {
			cnt = boundSvc.getInboundCount(keyfield, keyword);
		} else if (type == 1) {
			cnt = boundSvc.getOutboundCount(keyfield, keyword);
		} 
		return cnt;
	}
	
	public BoundBean getBound(int boundId) {
			return boundSvc.getBound(boundId);
	}
	
	public ViewBoundBean getBoundByType(int boundId, int type) {
		ViewBoundBean bean = null;
		
		if (type == 0) {
			bean = boundSvc.getInbound(boundId);
		} else if (type == 1) {
			bean = boundSvc.getOutbound(boundId);
		} 
		return bean;
	}           
	
	public Vector<ViewBoundBean> getBounds(String keyfield, String keyword, int start, int cnt, int type) {
		Vector<ViewBoundBean> vlists = new Vector<>();
		if (type == 0) {
			vlists = boundSvc.getInbounds(keyfield, keyword, start, cnt);
		} else if (type == 1) {
			vlists = boundSvc.getOutbounds(keyfield, keyword, start, cnt);
		} 
		return vlists;
	}
}
