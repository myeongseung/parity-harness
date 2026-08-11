package driver;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProgramIDGenerateDriver {
	public static void main(String[] args) {
		try {
			System.out.println(getSHA256HashValue("생산 설비 관리"));
			System.out.println(getSHA256HashValue("입고 관리"));
			System.out.println(getSHA256HashValue("출고 관리"));
			System.out.println(getSHA256HashValue("문서 관리"));
			System.out.println(getSHA256HashValue("부서 일정 등록"));
			System.out.println(getSHA256HashValue("근태 관리"));
			System.out.println(getSHA256HashValue("문서 작성"));
			System.out.println(getSHA256HashValue("전자 결재"));
			System.out.println(getSHA256HashValue("게시판 목록 조회"));
			System.out.println(getSHA256HashValue("쪽지"));
			System.out.println(getSHA256HashValue("구매 협력업체 관리"));
			System.out.println(getSHA256HashValue("영업 협력업체 관리"));
			System.out.println(getSHA256HashValue("공정 관리"));
			System.out.println(getSHA256HashValue("결재 리스트"));
			System.out.println(getSHA256HashValue("결재라인 관리"));
			System.out.println(getSHA256HashValue("원재료 관리"));
			System.out.println(getSHA256HashValue("가공품 관리"));
			System.out.println(getSHA256HashValue("출고 제품 관리"));
			System.out.println(getSHA256HashValue("수주 관리"));
			System.out.println(getSHA256HashValue("발주 관리"));
			System.out.println(getSHA256HashValue("프로필"));
			System.out.println(Long.MIN_VALUE | 8);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private static String getSHA256HashValue(String text)
			throws NoSuchAlgorithmException {
		return getHashValue(text, "SHA-256");
	}
	
	private static String getHashValue(String text, String algorithm)
			throws NoSuchAlgorithmException {
		StringBuilder sb = new StringBuilder();
		MessageDigest md = MessageDigest.getInstance(algorithm);
		
		md.update(text.getBytes());
		
		for (byte b : md.digest()) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}
}
