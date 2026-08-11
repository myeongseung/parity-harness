package model.view;

public class ViewTaskBean {
	private int id;
	private String userId;
	private String userName;
	private String deptName;
	private int companyId;
	private String companyName;
	private int documentId;
	private String subject;
	private int type;
	private String taskAt;
	private String createdAt;
	private int status;
	private String products;
	
	public ViewTaskBean() {
		super();
	}

	public ViewTaskBean(int id, String userId, String userName, String deptName, int companyId, String companyName,
			int documentId, String subject, int type, String taskAt, String createdAt, int status, String products
			) {
		super();
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.deptName = deptName;
		this.companyId = companyId;
		this.companyName = companyName;
		this.documentId = documentId;
		this.subject = subject;
		this.type = type;
		this.taskAt = taskAt;
		this.createdAt = createdAt;
		this.status = status;
		this.products = products;
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

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}
}
