package login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.PermissionController;
import controller.UserController;
import model.UserBean;

@WebServlet("/login/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private PermissionController permissionController;
	private UserController userController;
	
	public LoginServlet() {
		permissionController = new PermissionController();
		userController = new UserController();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		response.setContentType("text/html; charset=UTF-8");
		
		// 아이디, 비번 가져오기
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		String nextPage = "../permissionError.jsp";
		// 만약 로그인 객체를 보내지 않으면, 다시 로그인으로 보냄.
		
		if (id != null && password != null) {
			boolean result = userController.login(id, password);
			
			if (result) {
				// 로그인 성공
				nextPage = "../index.jsp";
				
				if (userController.isInitialLogin(id)) {
					// 최초 로그인 시, 세션에 아이디 등록 후 비밀번호 변경 시행
					session.setAttribute("tempId", id);
					nextPage = "changePassword.jsp";
				} else {
					UserBean user = userController.getUser(id);
					user.setPassword(null);
					user.setSocialNumber(null);
					session.setAttribute("user", user);

					// 관리자 권한을 가지고 있는 사용자면, 관리자 모드로 보냄.
					if (permissionController.isAdmin(session)) {
						nextPage = "../admin/admin.jsp";
					}
				}
			} else {
				// 로그인에 실패했을 경우, 로그인 실패 화면으로 보냄.
				nextPage = "passwordError.html";
			}
		}
		response.sendRedirect(nextPage);
	}
}
