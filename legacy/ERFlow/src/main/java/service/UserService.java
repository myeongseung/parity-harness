package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.UserBean;
import model.view.ViewUserBean;

public interface UserService extends Service {
	void logout(HttpSession session);

	boolean changePassword(String id, String password);

	boolean changePassword(UserBean user, String password);

	boolean deleteUser(HttpSession session, String userId);

	boolean deleteUser(HttpSession session, UserBean user);

	boolean register(HttpSession session, UserBean user);

	boolean register(HttpSession session, HttpServletRequest request);

	boolean updateUser(HttpSession session, UserBean user);

	int getUserTotalCount(String keyfield, String keyword);
	
	int getUserTotalCount(String keyfield1, String keyfield2, String keyword);

	UserBean getUser(String id);

	UserBean login(String id, String password);

	ViewUserBean getUserView(String id);

	Vector<UserBean> getUsers();

	Vector<ViewUserBean> getUserViews(String keyfield1, String keyfield2, String keyword);
	
	Vector<ViewUserBean> getUserViews(String keyfield1, String keyfield2, String keyword, int start, int cnt);

	Vector<ViewUserBean> getUserViews(String keyfield, String keyword, int start, int cnt);
}
