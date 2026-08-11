package model.view;

public class ViewCommentBean {
	private int id;
	private String userId;
	private String userName;
	private int postId;
	private String postSubject;
	private int commentRefId;
	private String comment;
	private int depth;
	private String createdAt;
	
	public ViewCommentBean() {
		super();
	}

	public ViewCommentBean(int id, String userId, String userName, int postId, String postSubject, int commentRefId,
			String comment, int depth, String createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.postId = postId;
		this.postSubject = postSubject;
		this.comment = comment;
		this.commentRefId = commentRefId;
		this.depth = depth;
		this.createdAt = createdAt;
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

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getPostSubject() {
		return postSubject;
	}

	public void setPostSubject(String postSubject) {
		this.postSubject = postSubject;
	}

	public int getCommentRefId() {
		return commentRefId;
	}

	public void setCommentRefId(int commentRefId) {
		this.commentRefId = commentRefId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}
