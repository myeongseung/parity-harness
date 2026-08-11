package controller;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import helper.WebHelper;
import model.CalendarBean;
import model.UserBean;
import model.view.ViewCalendarBean;
import service.implementation.CalendarServiceImpl;

public class CalendarController {
	private static final String PROGRAM_CODE = "822D0CAB0FFF374AAE6D095F0DF8612A3D2C14AB72B4AC1774D5DFD5FB5D29D0";
	
	private final CalendarServiceImpl calendarSvc;
	private final PermissionController permissionCon;
	
	public CalendarController() {
		calendarSvc = new CalendarServiceImpl();
		permissionCon = new PermissionController();
	}
	
	public Vector<ViewCalendarBean> getCalendarViews(HttpSession session) {
		return calendarSvc.getCalendarViews(session);
	}
	
	public boolean createCalendar(HttpSession session, CalendarBean bean) {
		boolean flag = false;
		
		if (session != null && bean != null) {
			if (WebHelper.isLogin(session)) {
				boolean isValid = false;
				
				switch (bean.getType()) {
					case 0 -> {
						isValid = true;
					}
					case 1 -> {
						isValid = permissionCon.hasProgramPermission(session, PROGRAM_CODE);
					}
					case 2 -> {
						isValid = permissionCon.isAdmin(session);
					}
				}
				if (isValid) {
					flag = calendarSvc.createCalendar(bean);
				}
			}
		}
		return flag;
	}
	
	public boolean updateCalendar(HttpSession session, CalendarBean bean) {
		boolean flag = false;
		
		if (session != null && bean != null) {
			if (WebHelper.isLogin(session)) {
				boolean isValid = false;
				
				switch (bean.getType()) {
					case 0 -> {
						isValid = true;
					}
					case 1 -> {
						isValid = permissionCon.hasProgramPermission(session, PROGRAM_CODE);
					}
					case 2 -> {
						isValid = permissionCon.isAdmin(session);
					}
				}
				if (isValid) {
					flag = calendarSvc.updateCalendar(bean);
				}
			}
		}		
		return flag;
	}
	
	public boolean deleteCalendar(HttpSession session, CalendarBean bean) {
		boolean flag = false;
		
		if (session != null && bean != null) {
			UserBean user = WebHelper.getValidUser(session);
			
			if (WebHelper.isLogin(session) && (user.getId().equals(bean.getUserId()))) {
				flag = calendarSvc.deleteCalendar(bean);
			}
		}
		return flag;
	}
}
