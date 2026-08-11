package graph;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import controller.ActivityController;
import controller.PermissionController;
import model.view.ViewWorkBean;

@WebServlet("/admin/graph/view")
public class GraphWorkViewServlet extends HttpServlet {
	private final ActivityController activityCon;
	private final PermissionController permissionCon;
	
	private static final long serialVersionUID = 1L;
	
    public GraphWorkViewServlet() {
        super();
        
        activityCon = new ActivityController();
        permissionCon = new PermissionController();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = (HttpSession)request.getSession();
		
		final String[] keys = { "결근", "출근", "퇴근", "조퇴", "지각", "반차", "연차" };
		final Map<Integer, Integer> values = new HashMap<>();
		
		if (!permissionCon.isAdmin(session)) {
			response.sendRedirect("../../permissionError.jsp");
			return;
		}
		final Gson gson = new Gson();
		final Vector<ViewWorkBean> works = activityCon.getWorkViews(LocalDate.now().toString());
		
		for (ViewWorkBean work : works) {
			int status = work.getStatus();
			
			if (values.containsKey(status)) {
				values.put(status, values.get(status) + 1);
			} else {
				values.put(status, 1);
			}
		}
		String json = gson.toJson(values);
		
		response.getWriter().write(json);
	}
}
