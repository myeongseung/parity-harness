package process;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controller.CalendarController;
import controller.ProcessController;
import helper.WebHelper;
import model.CalendarBean;
import model.ProcessBean;
import model.UserBean;

/**
 * Servlet implementation class CalendarInsertRequest
 */
@WebServlet("/process/processRegister")
public class ProcessRegisterRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ProcessController processCon;

	/**
	 * Default constructor.
	 */
	public ProcessRegisterRequest() {
		super();
		processCon = new ProcessController();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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

		JsonArray jsonArray = JsonParser.parseString(requestData).getAsJsonArray();

		for (int i = 1; i < jsonArray.size(); i++) {
			JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
			ProcessBean bean = new ProcessBean();
			String processId = jsonObject.get("processId").getAsString();
			String processName = jsonObject.get("processName").getAsString();
			String nextId = null;
			String prevId = null;
			int priority = i;
			bean.setId(processId);
			bean.setName(processName);
			bean.setNextId(nextId);
			bean.setPrevId(prevId);
			bean.setPriority(priority);
			
			if (i == 1) {
				JsonObject jsonNextObject = jsonArray.get(i+1).getAsJsonObject();
				nextId = jsonNextObject.get("processId").getAsString();
				bean.setNextId(nextId);
			} else if (i == jsonArray.size()-1) {
				JsonObject jsonPrevObject = jsonArray.get(i-1).getAsJsonObject();
				prevId = jsonPrevObject.get("processId").getAsString();
				bean.setPrevId(prevId);
			} else {
				JsonObject jsonNextObject = jsonArray.get(i+1).getAsJsonObject();
				JsonObject jsonPrevObject = jsonArray.get(i-1).getAsJsonObject();
				 nextId = jsonNextObject.get("processId").getAsString();
				 prevId = jsonPrevObject.get("processId").getAsString();
				bean.setNextId(nextId);
				bean.setPrevId(prevId);
			}
				
			result = processCon.createProcess(bean);
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
