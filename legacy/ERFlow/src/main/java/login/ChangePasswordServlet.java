package login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.UserController;

/**
 * Servlet implementation class ChangePassword
 */
@WebServlet("/login/ChangePassword")
public class ChangePasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final UserController userController;
	
	public ChangePasswordServlet() {
		super();
		userController = new UserController();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String id = (String)session.getAttribute("tempId");		
		String password = request.getParameter("password");
		String rePassword = request.getParameter("rePassword");
		String nextPage = "../permissionError.jsp";
		
		if (id != null && password != null && rePassword != null) {
			if (password.equals(rePassword) &&
					userController.changePassword(id, password)) {
				nextPage = "../login/passwordOk.html";
				session.removeAttribute("tempId");
				session.invalidate();
			}
		}
		response.sendRedirect(nextPage);
	}

}
