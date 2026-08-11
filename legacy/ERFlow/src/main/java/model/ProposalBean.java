package model;

public class ProposalBean {
	private long id;
	private long documentId;
	private String userId;
	private int routeId;
	private int step;
	private int result;
	private String comment;
	private String receivedAt;
	private String approvedAt;
	
	public ProposalBean() {
		super();
	}
	
	public ProposalBean(long id, long documentId, String userId, int routeId,
			int step, int result, String comment, String receivedAt,
			String approvedAt) {
		this.id = id;
		this.documentId = documentId;
		this.userId = userId;
		this.routeId = routeId;
		this.step = step;
		this.result = result;
		this.comment = comment;
		this.receivedAt = receivedAt;
		this.approvedAt = approvedAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
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

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(String receivedAt) {
		this.receivedAt = receivedAt;
	}

	public String getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(String approvedAt) {
		this.approvedAt = approvedAt;
	}
}
