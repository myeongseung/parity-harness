package controller;

import service.implementation.CodeServiceImpl;
import service.implementation.PasswordHashServiceImpl;

public class SecurityController {
	private final CodeServiceImpl codeSvc;
	private final PasswordHashServiceImpl hashSvc;
	
	public SecurityController() {
		codeSvc = new CodeServiceImpl();
		hashSvc = new PasswordHashServiceImpl();
	}
		
	public boolean createCode(String userId, int code) {
		return codeSvc.createCode(userId, code);
	}
	
	public boolean deleteCode(String userId) {
		return codeSvc.deleteCode(userId);
	}
	
	public boolean isValidCode(String userId, int code) {
		return codeSvc.isValidCode(userId, code);
	}
	
	public String decrypt(String text) {
		return hashSvc.decrypt(text);
	}
	
	public String encrypt(String text) {
		return hashSvc.encrypt(text);
	}
	
	public int generateCode() {
		return codeSvc.generateCode();
	}
}
