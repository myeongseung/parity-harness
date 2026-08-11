package driver;

import service.implementation.PasswordHashServiceImpl;

@Deprecated
/**
 * @author Ki-Seok Kang (@jUqItEr)
 * @deprecated
 * */
public class PasswordGenerateDriver {
	public static void main(String[] args) {
		System.out.print(generatePassword("20231006"));
	}
	
	public static String generatePassword(String password) {
		PasswordHashServiceImpl svc = new PasswordHashServiceImpl();
		
		return svc.generatePassword(password);
	}
}
