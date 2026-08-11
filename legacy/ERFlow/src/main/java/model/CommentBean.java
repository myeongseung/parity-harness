package model;

public class CommentBean {
	private int id;
	private int postId;
	private int refId;
	private String userId;
	private String comment;
	private int depth;
	private String createdAt;

	public CommentBean() {
		super();
	}
	
	public CommentBean(int id, int postId, int refId, String userId, String comment, int depth, String createdAt) {
		super();
		this.id = id;
		this.postId = postId;
		this.refId = refId;
		this.userId = userId;
		this.comment = comment;
		this.depth = depth;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
