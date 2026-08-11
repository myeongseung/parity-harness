package model.view;

public class ViewUnitBean {
	private String unitId;
	private String unitName;
	private int status;
	private String createdAt;
	private String userName;
	private String documentName;
	
	public ViewUnitBean() {
		super();
	}
	public ViewUnitBean(String unitId, String unitName, int status, String createdAt, String userName,
			String documentName) {
		this.unitId = unitId;
		this.unitName = unitName;
		this.status = status;
		this.createdAt = createdAt;
		this.userName = userName;
		this.documentName = documentName;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
}
