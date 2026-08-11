package model.view;

public class ViewProposalBean {
	private long id;
	private long documentId;
	private String subject;
	private String content;
	private String userId;
	private int step;
	private int result;
	private String comment;
	private String receivedAt;
	private String approvedAt;
	private String nickname;
	private int routeId;
	private String route;
	
	
	public ViewProposalBean() {
		super();
	}

	public ViewProposalBean(long id, long documentId, String subject, String content, String userId, int step,
			int result, String comment, String receivedAt, String approvedAt, String nickname, int routeId, String route) {
		super();
		this.id = id;
		this.documentId = documentId;
		this.subject = subject;
		this.content = content;
		this.userId = userId;
		this.step = step;
		this.result = result;
		this.comment = comment;
		this.receivedAt = receivedAt;
		this.approvedAt = approvedAt;
		this.nickname = nickname;
		this.routeId = routeId;
		this.route = route;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}
	
	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}
}
