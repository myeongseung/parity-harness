package mail;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.SecurityController;
import controller.UserController;
import model.UserBean;
import util.MailSender;

/**
 * Servlet implementation class mailServlet
 */
@WebServlet("/mail/SendMail")
public class SendMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final SecurityController securityController;
	private final UserController userController;
	
	public SendMailServlet() {
		super();
		securityController = new SecurityController();
		userController = new UserController();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		
		final String host = "https://[REDACTED_HOST]/ERFlow/mail/";
		final String dest = "MailVerification?authToken=";
		
		String id = request.getParameter("id");
		String email = request.getParameter("email");
		
		// Mail Content
		String title = "[ERFlow] 비밀번호 변경 안내 메일입니다.";
		String content = "<body>\r\n"
				+ "    <div class=\"container\" style=\"display: flex;\">\r\n"
				+ "        <div class=\"img\">\r\n"
				+ "            <img src=\"https://[REDACTED_HOST]/s/QFtKBJLTMQYx4RY/download/logo.png\" style=\"width: 100px; height: 100px; padding: 30px;\">\r\n"
				+ "        </div>\r\n"
				+ "        <div>\r\n"
				+ "            <p style=\"text-transform: uppercase; color: rgb(0, 176, 240); text-align:left; margin-top: 50px; font-size: 30px;\"><b>master</b></p>\r\n"
				+ "            <div style=\"margin-top: -20px;\">나에게</div>\r\n"
				+ "        </div>\r\n"
				+ "    </div>\r\n"
				+ "    <div class=\"middle\" style=\"display: flex; justify-content: space-around;\">\r\n"
				+ "        <div class=\"mail\">\r\n"
				+ "            <div class=\"title\" style=\"font-size: 50px;\">[이메일 링크 인증]</div>\r\n"
				+ "            <p>아래 링크를 클릭 하시면 이메일 인증이 완료됩니다. 30분 내에 접속해주세요.</p><br><br>\r\n"
				+ "            <a href=\"%s\" style=\"font-size: 20px;\">인증 바로가기</a><br>\r\n"
				+ "        </div>\r\n"
				+ "        <div class=\"announcement\"><p>Password Policy<br>After clicking the link, the password\r\n"
				+ "            must be 8 to 20 characters long.<br>At least one uppercase and lowercase \r\n"
				+ "           letters must be included<br>Characters and one or more numbers.\r\n"
				+ "           </p></div>\r\n"
				+ "        \r\n"
				+ "    </div>\r\n"
				+ "   \r\n"
				+ "</body>";
		String url = "../login/login.jsp";
		int code = securityController.generateCode();
		String parameter = "id=" + id + "&code=" + code;
		String mailUrl = host + dest + securityController.encrypt(parameter);
				
		if (id != null && email != null) {
			UserBean user = userController.getUser(id);
			
			securityController.deleteCode(id);
			securityController.createCode(id, code);
			
			if (user != null) {
				if(user.getEmail().equals(email)) {
					try {
			            MailSender.send(title, String.format(content, mailUrl), email);
			            url = "../login/sendOk.html";
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		response.sendRedirect(url);
	}
}
