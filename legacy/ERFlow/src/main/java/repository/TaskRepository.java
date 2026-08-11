package repository;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

public class TaskRepository {
	private static volatile TaskRepository repository = null;
	
	private static HashMap<Integer, String> taskType;
	private static HashMap<Integer, String> taskStatus;
	
	private final int[] taskTypeKeys = {
		0, 1
	};
	private final String[] taskTypeValues = {
		"수주", "발주"
	};

	private final int[] taskStatusKeys = {
		1, 2, 3
	};
	private final String[] taskStatusValues = {
		"진행", "완료", "미확인"
	};
	
	private TaskRepository() {
		taskType = new HashMap<>();
		taskStatus = new HashMap<>();
		
		for (int i = 0; i < taskTypeKeys.length; ++i) {
			taskType.put(taskTypeKeys[i], taskTypeValues[i]);
		}
		for (int i = 0; i < taskStatusKeys.length; ++i) {
			taskStatus.put(taskStatusKeys[i], taskStatusValues[i]);
		}
	}
	
	public static TaskRepository getInstance() {
		if (repository == null) {
			synchronized (TaskRepository.class) {
				repository = new TaskRepository();
			}
		}
		return repository;
	}
	
	public boolean containsTaskTypeCode(int code) {
		return taskType.containsKey(code);
	}
	
	public boolean containsTaskStatusCode(int code) {
		return taskStatus.containsKey(code);
	}
	
	public boolean containsTaskTypeValue(String name) {
		return taskType.containsValue(name);
	}

	public boolean containsTaskStatusValue(String name) {
		return taskStatus.containsValue(name);
	}
	
	public String getTaskTypeCode(int code) {
		return taskType.get(code);
	}
	
	public String getTaskStatusCode(int code) {
		return taskStatus.get(code);
	}
	
	public Set<Entry<Integer, String>> getTaskTypeEntries() {
		return taskType.entrySet();
	}
	
	public Set<Entry<Integer, String>> getTaskStatusEntries() {
		return taskStatus.entrySet();
	}
	
	public Set<Integer> getTaskTypeKeys() {
		return taskType.keySet();
	}
	
	public Set<Integer> getTaskStatusKeys() {
		return taskStatus.keySet();
	}
}
