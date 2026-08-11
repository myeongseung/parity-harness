package calendar;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import controller.CalendarController;
import helper.WebHelper;
import model.view.ViewCalendarBean;

@WebServlet("/calendar/view")
public class CalendarViewRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final CalendarController calendarCon;
	
	public CalendarViewRequest() {
		calendarCon = new CalendarController();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		
		HttpSession session = request.getSession();
		
		if (!WebHelper.isLogin(session)) {
			response.sendRedirect("../permissionError.jsp");
			return;
		}
		Vector<ViewCalendarBean> vlist = calendarCon.getCalendarViews(session);
		
		Gson gson = new Gson();
	    String json = gson.toJson(vlist);
	    
	    response.getWriter().write(json);
	    
	}
}
