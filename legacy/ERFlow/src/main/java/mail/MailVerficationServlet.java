package mail;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.SecurityController;

@WebServlet("/mail/MailVerification")
public class MailVerficationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final SecurityController securityController;
	
	public MailVerficationServlet() {
		super();
		securityController = new SecurityController();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String query = request.getParameter("authToken");
		String nextPage = "../accessError.jsp";
		
		if (query != null) {
			try {
				query = query.replace(" ", "+");
				query = securityController.decrypt(query);
				
				HashMap<String, String> maps = new HashMap<>();
				String[] params = query.split("&");
				
				for (String param : params) {
					String[] values = param.split("=");
					
					maps.put(values[0], values[1]);
				}
				String id = maps.get("id");
				int code = Integer.parseInt(maps.get("code"));
				
				if (securityController.isValidCode(id, code)) {
					HttpSession session = request.getSession();
					
					session.setAttribute("tempId", id);
					
					securityController.deleteCode(id);
					
					nextPage = "../login/changePassword.jsp";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		response.sendRedirect(nextPage);
	}
}
