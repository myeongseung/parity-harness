package service.implementation;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import service.PasswordHashService;

/**
 * <p>
 *     Service to generate SHA-256 hash password for storing database management systems.<br>
 *     This class should be implement to 'PasswordHashServiceImpl'.
 * </p>
 *
 * @author     jUqItEr (Ki-seok Kang)
 * @version    1.2.1
 * */
public class PasswordHashServiceImpl implements PasswordHashService {
	private static final int GCM_IV_LENGTH = 16;
	private static final int GCM_KEY_LENGTH = 16;
	private static final int GCM_TAG_LENGTH= 128;
    private static final int SALT_LENGTH = 20;
    private static final int KEY_STRETCH_COUNT = 10;

    private final Base64.Decoder decoder;
    private final Base64.Encoder encoder;

    public PasswordHashServiceImpl() {
        this.decoder = Base64.getDecoder();
        this.encoder = Base64.getEncoder();
    }   // -- End of constructor

    private boolean isValidBase64String(String text) {
        Pattern pattern = Pattern.compile("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }   // -- End of function (isValidBase64String)

    private String decodeToBase64String(String text) {
        byte[] decodedBytes = decoder.decode(text);
        return new String(decodedBytes);
    }   // -- End of function (decodeToBase64String)

    private String encodeToBase64String(String text) {
        return encoder.encodeToString(text.getBytes());
    }   // -- End of function (encodeToBase64String)

    private String getHashValue(String text, String algorithm) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance(algorithm);

        md.update(text.getBytes());

        for (byte b : md.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String getSha256HashValue(String text) throws NoSuchAlgorithmException {
        return getHashValue(text, "SHA-256");
    }

    private byte[] getIvByteArray() {
    	SecureRandom sr = new SecureRandom();
    	byte[] iv = new byte[GCM_IV_LENGTH];
    	
    	sr.nextBytes(iv);
    	return iv;
    }
    
    private byte[] getKeyByteArray() {
    	SecureRandom sr = new SecureRandom();
    	byte[] key = new byte[GCM_KEY_LENGTH];
    	
    	sr.nextBytes(key);
    	return key;
    }
    
    private byte[] getSaltByteArray() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];

        sr.nextBytes(salt);
        return salt;
    }   // -- End of function (getSaltByteArray)

    private ByteArrayInputStream getSaltByteStream() {
        return new ByteArrayInputStream(getSaltByteArray());
    }   // -- End of function (getSaltByteStream)


	@Override
    public boolean isValidPassword(String pwd, String encodedString) {
        boolean flag = encodedString != null &&
        		isValidBase64String(encodedString);

        if (flag) {
            String decodedString = decodeToBase64String(encodedString);
            String salt = decodedString.substring(0, 40);
            String test = null;
            flag = encodedString.equals(test = generatePassword(pwd, salt));
        }   // -- End of if
        return flag;
    }   // -- End of function (isValidPassword)

	@Override
	public String decrypt(String text) {
		String result = text;
		
		if (result != null) {
			byte[] content = Base64.getDecoder().decode(result);
			byte[] key = Arrays.copyOfRange(content, 0, GCM_KEY_LENGTH);
			byte[] iv = Arrays.copyOfRange(content, GCM_KEY_LENGTH, GCM_KEY_LENGTH + GCM_IV_LENGTH);
			byte[] cipherText = Arrays.copyOfRange(content, GCM_KEY_LENGTH + GCM_IV_LENGTH,
					content.length - 1);
			
			result = decrypt(cipherText, iv, key);
		}
		return result;
	}
	
	@Override
	public String decrypt(byte[] text, byte[] iv, byte[] key) {
		String result = null;
		
		if (text != null) {
			try {
				Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
				SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
				GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
				
				cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
				
				result = new String(cipher.doFinal(text));
			} catch (Exception e) {
				e.printStackTrace();
				result = null;
			}
		}
		return result;
 	}
	
	@Override
	public String encrypt(String text) {
		byte[] iv = getIvByteArray();
		byte[] key = getKeyByteArray();
		
		return encrypt(text, iv, key);
	}
	
	@Override
	public String encrypt(String text, byte[] iv, byte[] key) {
		String result = text;
		
		if (result != null) {
			try {
				Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
				SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
				GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
				byte[] cipherText = null;
				byte[] encryptedData = null;
				
				cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
				
				cipherText = cipher.doFinal(text.getBytes());
				
				encryptedData = Arrays.copyOf(key, key.length + iv.length + cipherText.length + 1);
				System.arraycopy(iv, 0, encryptedData, key.length, iv.length);
				System.arraycopy(cipherText, 0, encryptedData, key.length + iv.length, cipherText.length);
				
				result = new String(Base64.getEncoder().encode(encryptedData));
			} catch (Exception e) {
				e.printStackTrace();
				result = null;
			}
		}
		return result;
	}
		
    @Override
    public String generatePassword(String pwd) {
        String salt = getSalt();
        return generatePassword(pwd, salt);
    }   // -- End of function (generatePassword)

    @Override
    public String generatePassword(String pwd, String salt) {
        String newPassword = pwd + salt;

        try {
            /* 키 스트레칭 수행 */
            for (int i = 0; i < KEY_STRETCH_COUNT; ++i) {
                newPassword = getSha256HashValue(newPassword);
            }   // -- End of for-loop
            /* 다 끝났으면 base64로 인코딩 수행 */
            newPassword = encodeToBase64String(salt + newPassword);
        } catch (NoSuchAlgorithmException e) {
            /* 오류가 나면 비밀번호를 생성하면 안 됨 */
            newPassword = null;
            e.printStackTrace();
        }   // -- End of try-catch
        return newPassword;
    }   // -- End of function (generatePassword)

    @Override
    public String getSalt() {
        StringBuilder sb = new StringBuilder();
        ByteArrayInputStream saltStream = getSaltByteStream();
        IntStream stream = IntStream.generate(saltStream::read)
                .limit(saltStream.available());
        stream.forEach(value -> sb.append(String.format("%02x", value)));
        return sb.toString();
    }   // -- End of function (getSalt)
}   // -- End of class