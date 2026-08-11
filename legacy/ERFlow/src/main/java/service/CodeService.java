package service;

public interface CodeService extends Service {
	boolean createCode(String userId, int code);
	boolean deleteCode(String userId);
	
	boolean isValidCode(String userId, int code);
	
	int generateCode();
}
