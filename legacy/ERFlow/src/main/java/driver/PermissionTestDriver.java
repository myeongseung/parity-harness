package driver;

import controller.UserController;
import model.UserBean;
import service.implementation.PermissionServiceImpl;

public class PermissionTestDriver {
	
	public static void main(String[] args) {
		String userId = "2310130";
		UserController controller = new UserController();
		UserBean user = controller.getUser(userId);
		
		System.out.println("Is admin privilege?: " + isAdmin(user));
	}
	
	public static boolean isAdmin(UserBean user) {
		PermissionServiceImpl permissionSvc = new PermissionServiceImpl();
		return permissionSvc.isAdmin(user);
	}
}
