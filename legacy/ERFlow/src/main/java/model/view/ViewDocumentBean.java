package model.view;

public class ViewDocumentBean {
	private long id;
	private String subject;
	private String content;
	private String templateName;
	private String createdAt;
	private String updatedAt;
	private String userId;
	private String userName;
	private String deptName;
	private String jobName;
	private String routeTitle;
	private int routeId;
	private String routeName;
	private int documentStatus;
	private int proposalStatus;
	
	public ViewDocumentBean() {
		super();
	}

	public ViewDocumentBean(long id, String subject, String content, String templateName, String createdAt,
			String updatedAt, String userId, String userName, String deptName, String jobName, String routeTitle,
			int routeId, String routeName, int documentStatus, int proposalStatus) {
		super();
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.templateName = templateName;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.userId = userId;
		this.userName = userName;
		this.deptName = deptName;
		this.jobName = jobName;
		this.routeTitle = routeTitle;
		this.routeId = routeId;
		this.routeName = routeName;
		this.documentStatus = documentStatus;
		this.proposalStatus = proposalStatus;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getRouteTitle() {
		return routeTitle;
	}

	public void setRouteTitle(String routeTitle) {
		this.routeTitle = routeTitle;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
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
}
