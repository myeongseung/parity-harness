package controller;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.UserBean;
import model.view.ViewUserBean;
import service.implementation.PasswordHashServiceImpl;
import service.implementation.UserServiceImpl;

public class UserController {
	private PasswordHashServiceImpl hashSvc = null;
	private UserServiceImpl userSvc = null;
	
	public UserController() {
		hashSvc = new PasswordHashServiceImpl();
		userSvc = new UserServiceImpl();
	}
	
	public void logout(HttpSession session) {
		userSvc.logout(session);
	}
	
	public boolean changePassword(String userId, String password) {
		return userSvc.changePassword(userId, password);
	}
	
	public boolean deleteUser(HttpSession session, String userId) {
		return userSvc.deleteUser(session, userId);
	}
	
	public boolean register(HttpSession session, UserBean user) {
		return userSvc.register(session, user);
	}
	
	public boolean updateUser(HttpSession session, UserBean user) {
		return userSvc.updateUser(session, user);
	}
	
	public boolean login(String id, String password) {	
		return userSvc.login(id, password) != null;
	}
	
	public boolean isInitialLogin(String id) {
		UserBean user = userSvc.getUser(id);
		boolean result = false;
		
		if (user != null) {
			String password = user.getPassword();
			result = hashSvc.isValidPassword(id, password);
		}
		return result;
	}
	
	public int getUserTotalCount(String keyfield, String keyword) {
		int count = userSvc.getUserTotalCount(keyfield, keyword);
		
		return count;
	}
	
	public int getUserTotalCount(String keyfield1, String keyfield2, String keyword) {
		int count = userSvc.getUserTotalCount(keyfield1, keyfield2, keyword);
		
		return count;
	}
	
	public UserBean getUser(String id) {
		UserBean user = userSvc.getUser(id);
		
		return user;
	}
	
	public ViewUserBean getUserView(String id) {
		ViewUserBean user = userSvc.getUserView(id);
		
		return user;
	}
	
	public Vector<UserBean> getUsers() {
		Vector<UserBean> users = userSvc.getUsers();
		
		return users;
	}
	
	public Vector<ViewUserBean> getUserViews(String keyfield1, String keyfield2, String keyword) {
		Vector<ViewUserBean> users = userSvc.getUserViews(keyfield1, keyfield2, keyword);
		
		return users;
	}
	
	public Vector<ViewUserBean> getUserViews(String keyfield1, String keyfield2, String keyword, int start, int cnt) {
		Vector<ViewUserBean> users = userSvc.getUserViews(keyfield1, keyfield2, keyword, start, cnt);
		
		return users;
	}
	
	public Vector<ViewUserBean> getUserViews(String keyfield, String keyword, int start, int cnt) {
		Vector<ViewUserBean> users = userSvc.getUserViews(keyfield, keyword, start, cnt);
		
		return users;
	}
	
}
