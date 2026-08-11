package calendar;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

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

/**
 * Servlet implementation class CalendarInsertRequest
 */
@WebServlet("/calendar/insert")
public class CalendarRegisterRequest extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CalendarController calendarCon;

    /**
     * Default constructor.
     */
    public CalendarRegisterRequest() {
        super();
        calendarCon = new CalendarController();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
        
    	DateTimeFormatter dtf = new DateTimeFormatterBuilder()
    			.appendPattern("[yyyy-MM-dd HH:mm:ss]")
    			.appendPattern("[yyyy-MM-dd'A'HH:mm]")
    			.appendPattern("[yyyy-MM-dd'T'HH:mm]")
    			.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
    			.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
    			.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
    			.toFormatter();

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
        
        // Parse the JSON object to get the new event details
        String userId = jsonObject.get("userId").getAsString();
        String subject = jsonObject.get("subject").getAsString();
        String content = jsonObject.get("content").getAsString();
        String start = jsonObject.get("start").getAsString();
        String end = jsonObject.get("end").getAsString();
        int type = jsonObject.get("type").getAsInt();
        
        try {
        	LocalDateTime ldt = LocalDateTime.parse(end, dtf);
        } catch (DateTimeParseException e) {
        	e.printStackTrace();
        	end = null;
        }
        if (user.getId().equals(userId)) {
            CalendarBean bean = new CalendarBean();
            bean.setUserId(userId);
            bean.setSubject(subject);
            bean.setContent(content);
            bean.setStartedAt(start);
            bean.setEndedAt(end);
            bean.setType(type);
            
            result = calendarCon.createCalendar(session, bean);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject responseJson = new JsonObject();

        final String[] results = { "error", "success" };
        final String[] messages = { "이벤트 추가에 실패했습니다.", "이벤트가 성공적으로 추가되었습니다." };

        int switcher = result ? 1 : 0;

        responseJson.addProperty("status", results[switcher]);
        responseJson.addProperty("message", messages[switcher]);

        response.getWriter().write(responseJson.toString());
    }
}
