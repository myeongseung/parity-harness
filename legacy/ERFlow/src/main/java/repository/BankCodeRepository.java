package repository;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class BankCodeRepository {
	private static volatile BankCodeRepository repository;
	
	private static HashMap<String, String> bankCode;
	
	private final String[] keys = new String[] {
		"001", "002", "003", "004", "005", 
		"007", "008", "011", "012", "020", 
		"023", "026", "027", "031", "032", 
		"034", "035", "037", "039", "045", 
		"048", "050", "051", "052", "054", 
		"055", "056", "057", "058", "059", 
		"060", "061", "062", "063", "064", 
		"065", "071", "076", "077", "081", 
		"088", "089", "090", "092", "093",
		"094", "095", "099", "209", "218",
		"230", "238", "240", "243", "247",
		"261", "262", "263", "264", "265",
		"266", "267", "268", "269", "270",
		"278", "279", "280", "287", "289",
		"290", "291", "292"
	};
	private final String[] values = new String[] {
		"한국은행", "산업은행", "기업은행", "국민은행", "외환은행", 
		"수협은행", "수출입은행", "농협은행", "농협회원조합", "우리은행", 
		"SC제일은행", "서울은행", "한국씨티은행", "대구은행", "부산은행" , 
		"광주은행", "제주은행", "전북은행", "경남은행", "새마을금고연합회", 
		"신협중앙회", "상호저축은행", "기타 외국계은행", "모건스탠리은행", "HSBC은행", 
		"도이치은행", "알비에스피엘씨은행", "제이피모간체이스은행", "미즈호코퍼레이트은행", "미쓰비시도쿄UFJ은행", 
		"BOA", "비엔피파리바은행", "중국공상은행", "중국은행", "산림조합", 
		"대화은행", "우체국", "신용보증기금", "기술신용보증기금", "하나은행", 
		"신한은행", "케이뱅크", "카카오뱅크", "토스뱅크", "한국주택금융공사",
		"서울보증보험", "경찰청", "금융결제원", "동양종합금융증권", "현대증권" ,
		"미래에셋증권", "대우증권" , "삼성증권", "한국투자증권", "NH투자증권",
		"교보증권", "하이투자증권", "에이치엠씨투자증권", "키움증권", "이트레이드증권",
		"SK증권", "대신증권", "솔로몬투자증권", "한화증권", "하나대투증권",
		"신한금융투자", "동부증권", "유진투자증권", "메리츠증권", "엔에이치투자증권",
		"부국증권", "신영증권", "엘아이지투자증권"
	};
	
	private BankCodeRepository() {
		bankCode = new HashMap<>();

		for (int i = 0; i < keys.length; ++i) {
			bankCode.put(keys[i], values[i]);
		}
	}
	
	public static BankCodeRepository getInstance() {
		if (repository == null) {
			synchronized (BankCodeRepository.class) {
				repository = new BankCodeRepository();
			}
		}
		return repository;
	}
	
	public boolean containsBankCode(String code) {
		return bankCode.containsKey(code);
	}
	
	public boolean containsBankName(String name) {
		return bankCode.containsValue(name);
	}
	
	public String getBankName(String code) {
		return bankCode.get(code);
	}
	
	public Set<Entry<String, String>> getEntries() {
		return bankCode.entrySet();
	}
	
	public Set<String> getBankCodeKeys() {
		return bankCode.keySet();
	}
}
