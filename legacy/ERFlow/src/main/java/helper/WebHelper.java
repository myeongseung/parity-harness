package helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import controller.PermissionController;
import model.UserBean;

public class WebHelper {
	private static final PermissionController permissionCon = new PermissionController();
	
	public static boolean isLogin(HttpSession session) {
		UserBean user = getValidUser(session);
		
		return user != null;
	}
	
	public static boolean isAuthorizedUser(HttpSession session, String userId) {
		UserBean currentUser = getValidUser(session);
				
		return (currentUser != null && userId != null &&
				currentUser.getId().equals(userId)) ||
				permissionCon.isAdmin(session);
	}
	
	public static boolean isAuthorizedUser(HttpSession session, UserBean comparable) {
		boolean result = false;
		
		if (comparable != null) {
			result = isAuthorizedUser(session, comparable.getId());
		}
		return result;
	}
	
	public static int parseInt(HttpServletRequest request, String name) {
		String target = request.getParameter(name);
		int result = 0;
		
		if (target != null) {
			result = Integer.parseInt(target);
		}
		return result;
	}
	
	public static long parseLong(HttpServletRequest request, String name) {
		String target = request.getParameter(name);
		long result = 0;
		
		if (target != null) {
			result = Long.parseLong(target);
		}
		return result;
	}
	
	public static String getDate(String dateString) throws ParseException {
		SimpleDateFormat inSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat outSdf = new SimpleDateFormat("yyyy년 MM월 dd일");
		Date date = inSdf.parse(dateString);
		
		return outSdf.format(date);
	}
	
	/*
	 * 파일 확장자 추출을 위한 메소드
	 *
	 * @author 곽성원
	 * */
	public static String extractFileExtension(String filename) {
		String result = "";
		int index = -1;
		
		if ((index = filename.lastIndexOf(".")) != -1) {
			result = filename.substring(index + 1);
		}
		return result;
	}
	
	/*
	 * 파일 이름 추출을 위한 메소드
	 *
	 * @author 곽성원
	 * */
	public static String extractFileName(String filename) {
		String result = "";
		int index = -1;
		
		if ((index = filename.lastIndexOf(".")) != -1) {
			result = filename.substring(0, index);
		}
		return result;
	}
	
	public static UserBean getValidUser(HttpSession session) {
		UserBean user = null;
		
		if (session != null) {
			user = (UserBean)session.getAttribute("user");
		}
		return user;
	}
}
