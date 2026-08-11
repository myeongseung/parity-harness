package driver;

import controller.SecurityController;

public class DataSecurityDriver {
	public static void main(String[] args) {
		SecurityController cont = new SecurityController();
		String encrypted = cont.encrypt("id=admin&code=111111");
		
		System.out.println("암호화 하면 " + encrypted);
		System.out.println("복호화 하면 " + cont.decrypt(encrypted));
	}
}
