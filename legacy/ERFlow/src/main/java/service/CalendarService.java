package service;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.CalendarBean;
import model.view.ViewCalendarBean;

public interface CalendarService extends Service {
	Vector<ViewCalendarBean> getCalendarViews(HttpSession session);
	
	boolean createCalendar(CalendarBean bean);
	boolean updateCalendar(CalendarBean bean);
	boolean deleteCalendar(CalendarBean bean);
}