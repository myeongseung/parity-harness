package model;

public class UnitBean {
	private String id;
	private String chargerId;
	private long documentId;
	private String name;
	private int status;
	private String createAt;
	
	public UnitBean() {
		super();
	}
	
	public UnitBean(String id, String chargerId, long documentId, String name, int status,
			String createAt) {
		this.id = id;
		this.chargerId = chargerId;
		this.documentId = documentId;
		this.name = name;
		this.status = status;
		this.createAt = createAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChargerId() {
		return chargerId;
	}

	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

}
