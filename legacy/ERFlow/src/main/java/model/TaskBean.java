package model;

public class TaskBean {
	private int id;
	private String userId;
	private int companyId;
	private int documentId;
	private int type;
	private String taskAt;
	private String createdAt;
	private int status;
	
	public TaskBean() {
		super();
	}
	
	public TaskBean(int id, String userId, int companyId, int documentId,
			int type, String taskAt, String createdAt, int status) {
		this.id = id;
		this.userId = userId;
		this.companyId = companyId;
		this.documentId = documentId;
		this.type = type;
		this.taskAt = taskAt;
		this.createdAt = createdAt;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTaskAt() {
		return taskAt;
	}

	public void setTaskAt(String taskAt) {
		this.taskAt = taskAt;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
