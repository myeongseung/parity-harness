package model;

public class PostBean {
	private int id;
	private String userId;
	private int boardId;
	private int refId;
	private String subject;
	private String content;
	private int depth;
	private int pos;
	private int count;
	private int delete;
	private String createdAt;
	
	public PostBean() {
		super();
	}

	public PostBean(int id, String userId, int boardId, int refId, String subject, String content, int depth, int pos,
			int count, int delete, String createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.boardId = boardId;
		this.refId = refId;
		this.subject = subject;
		this.content = content;
		this.depth = depth;
		this.pos = pos;
		this.count = count;
		this.delete = delete;
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

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
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

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getDelete() {
		return delete;
	}

	public void setDelete(int delete) {
		this.delete = delete;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}
