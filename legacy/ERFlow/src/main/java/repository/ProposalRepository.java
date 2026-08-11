package repository;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

public class ProposalRepository {
	private static volatile ProposalRepository repository;
	
	private static HashMap<Integer, String> proposalStatus;
	
	private final int[] proposalKeys = {
		0, 1, 2, 3
	};
	private final String[] proposalValues = {
		"결재 대기 중", "승인", "반려", "결재 진행 중"
	};
	
	private ProposalRepository() {
		proposalStatus = new HashMap<>();

		for (int i = 0; i < proposalKeys.length; ++i) {
			proposalStatus.put(proposalKeys[i], proposalValues[i]);
		}
	}
	
	public static ProposalRepository getInstance() {
		if (repository == null) {
			synchronized (ProposalRepository.class) {
				repository = new ProposalRepository();
			}
		}
		return repository;
	}
	
	public boolean containsProposalCode(int code) {
		return proposalStatus.containsKey(code);
	}
	
	public boolean containsProposalValue(String name) {
		return proposalStatus.containsValue(name);
	}
	
	public String getProposalCode(int code) {
		return proposalStatus.get(code);
	}
	
	public Set<Entry<Integer, String>> getEntries() {
		return proposalStatus.entrySet();
	}
	
	public Set<Integer> getBankCodeKeys() {
		return proposalStatus.keySet();
	}
}
