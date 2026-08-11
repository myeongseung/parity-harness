package model;

public class DocumentBean {
	private long id;
	private String userId;
	private int routeId;
	private int templateId;
	private String subject;
	private String content;
	private int documentStatus;
	private int proposalStatus;
	private String createdAt;
	private String updatedAt;
	
	public DocumentBean() {
		super();
	}

	public DocumentBean(long id, String userId, int routeId, int templateId, String subject, String content,
			int documentStatus, int proposalStatus, String createdAt, String updatedAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.routeId = routeId;
		this.templateId = templateId;
		this.subject = subject;
		this.content = content;
		this.documentStatus = documentStatus;
		this.proposalStatus = proposalStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(int documentStatus) {
		this.documentStatus = documentStatus;
	}

	public int getProposalStatus() {
		return proposalStatus;
	}

	public void setProposalStatus(int proposalStatus) {
		this.proposalStatus = proposalStatus;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
}
