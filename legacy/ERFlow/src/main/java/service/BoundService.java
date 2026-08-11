package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.BoundBean;
import model.CompanyBean;
import model.view.ViewBoundBean;

public interface BoundService extends Service {
	boolean createInbound(HttpServletRequest request);
	boolean createInbound(BoundBean bound);
	boolean deleteInbound(int boundId);
	boolean updateInbound(HttpServletRequest request);
	boolean updateInbound(BoundBean bound);
	
	ViewBoundBean getInbound(int boundId);
	
	Vector<ViewBoundBean> getInbounds(String keyfield, String keyword , int start, int cnt);
	
	int getInboundCount(String keyfield, String keyword);
	
	boolean createOutbound(HttpServletRequest request);
	boolean createOutbound(BoundBean bound);
	boolean deleteOutbound(int boundId);
	boolean updateOutbound(HttpServletRequest request);
	boolean updateOutbound(BoundBean bound);
	
	ViewBoundBean getOutbound(int boundId);
	
	Vector<ViewBoundBean> getOutbounds(String keyfield, String keyword , int start, int cnt);
	
	int getOutboundCount(String keyfield, String keyword);
	
	BoundBean getBound(int boundId);
}
