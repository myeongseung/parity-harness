package model.view;

public class ViewPostBean {
	private int id;
	private int boardId;
	private String userId;
	private String name;
	private int refId;
	private String subject;
	private String content;
	private int depth;
	private int pos;
	private int count;
	private String createdAt;
	private int delete;
	
	public ViewPostBean() {
		super();
	}

	public ViewPostBean(int id, int boardId, String userId, String name, int refId, String subject, String content,
			int depth, int pos, int count, String createdAt, int delete) {
		super();
		this.id = id;
		this.boardId = boardId;
		this.userId = userId;
		this.name = name;
		this.refId = refId;
		this.subject = subject;
		this.content = content;
		this.depth = depth;
		this.pos = pos;
		this.count = count;
		this.createdAt = createdAt;
		this.delete = delete;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public int getDelete() {
		return delete;
	}

	public void setDelete(int delete) {
		this.delete = delete;
	}
}
