package model;

public class MessageBean {
	private int id;
	private String senderId;
	private String receiverId;
	private String content;
	private String createdAt;
	private String readAt;
	private int readStatus;
	private int isSenderVisible;
	private int isReceiverVisible;
	
	public MessageBean() {
		super();
	}

	public MessageBean(int id, String senderId, String receiverId, String content, String createdAt, String readAt,
			int readStatus, int isSenderVisible, int isReceiverVisible) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = content;
		this.createdAt = createdAt;
		this.readAt = readAt;
		this.readStatus = readStatus;
		this.isSenderVisible = isSenderVisible;
		this.isReceiverVisible = isReceiverVisible;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getReadAt() {
		return readAt;
	}

	public void setReadAt(String readAt) {
		this.readAt = readAt;
	}

	public int getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}

	public int getIsSenderVisible() {
		return isSenderVisible;
	}

	public void setIsSenderVisible(int isSenderVisible) {
		this.isSenderVisible = isSenderVisible;
	}

	public int getIsReceiverVisible() {
		return isReceiverVisible;
	}

	public void setIsReceiverVisible(int isReceiverVisible) {
		this.isReceiverVisible = isReceiverVisible;
	}
}