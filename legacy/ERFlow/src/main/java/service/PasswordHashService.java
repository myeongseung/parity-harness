package service;

/**
 * <p>
 *     The implementation for 'PasswordHashService'.
 * </p>
 *
 * @author      jUqItEr (Ki-seok Kang)
 * @version     1.1.0
 * */
public interface PasswordHashService extends Service {
    boolean isValidPassword(String pwd, String encodedString);
    
    String decrypt(String text);
    String decrypt(byte[] text, byte[] iv, byte[] key);
    String encrypt(String text);
    String encrypt(String text, byte[] iv, byte[] key);

    String generatePassword(String pwd);
    String generatePassword(String pwd, String salt);
    String getSalt();
}
