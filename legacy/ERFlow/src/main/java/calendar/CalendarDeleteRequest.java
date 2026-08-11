package calendar;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controller.CalendarController;
import helper.WebHelper;
import model.CalendarBean;
import model.UserBean;
import model.view.ViewCalendarBean;

/**
 * Servlet implementation class CalendarDeleteRequest
 */
@WebServlet("/calendar/delete")
public class CalendarDeleteRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CalendarController calendarCon;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CalendarDeleteRequest() {
        super();
        calendarCon = new CalendarController();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
    	
    	UserBean user = WebHelper.getValidUser(session);
    	boolean result = false;

        if (!WebHelper.isLogin(session)) {
            response.sendRedirect("../permissionError.jsp");
            return;
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        String requestData = sb.toString();
        JsonObject jsonObject = new JsonParser().parse(requestData).getAsJsonObject();
        
        
        String userId = jsonObject.get("userId").getAsString();
        int eventId = Integer.parseInt(jsonObject.get("eventId").getAsString());
        
        if (user.getId().equals(userId)) {
        	CalendarBean bean = new CalendarBean();
        	bean.setId(eventId);
        	bean.setUserId(userId);
        	
        	result = calendarCon.deleteCalendar(session, bean);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject responseJson = new JsonObject();
        
        final String[] results = { "error", "success" };
        final String[] messages = { "이벤트 삭제에 실패했습니다.", "이벤트가 성공적으로 삭제되었습니다." };

        int switcher = result ? 1 : 0;
        
        responseJson.addProperty("status", results[switcher]);
        responseJson.addProperty("message", messages[switcher]);
        
        response.getWriter().write(responseJson.toString());
    }
}
