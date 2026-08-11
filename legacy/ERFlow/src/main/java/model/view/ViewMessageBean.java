package model.view;

public class ViewMessageBean {
	private int id;
	private String receiverId;
	private String receiverName;
	private String receiverDeptName;
	private String receiverJobName;
	private String senderId;
	private String senderName;
	private String senderDeptName;
	private String senderJobName;
	private String content;
	private String createdAt;
	private String readAt;
	private int readStatus;
	private int isSenderVisible;
	private int isReceiverVisible;
	
	public ViewMessageBean() {
		super();
	}

	public ViewMessageBean(int id, String receiverId, String receiverName, String receiverDeptName,
			String receiverJobName, String senderId, String senderName, String senderDeptName, String senderJobName,
			String content, String createdAt, String readAt, int readStatus, int isSenderVisible,
			int isReceiverVisible) {
		super();
		this.id = id;
		this.receiverId = receiverId;
		this.receiverName = receiverName;
		this.receiverDeptName = receiverDeptName;
		this.receiverJobName = receiverJobName;
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderDeptName = senderDeptName;
		this.senderJobName = senderJobName;
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

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverDeptName() {
		return receiverDeptName;
	}

	public void setReceiverDeptName(String receiverDeptName) {
		this.receiverDeptName = receiverDeptName;
	}

	public String getReceiverJobName() {
		return receiverJobName;
	}

	public void setReceiverJobName(String receiverJobName) {
		this.receiverJobName = receiverJobName;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderDeptName() {
		return senderDeptName;
	}

	public void setSenderDeptName(String senderDeptName) {
		this.senderDeptName = senderDeptName;
	}

	public String getSenderJobName() {
		return senderJobName;
	}

	public void setSenderJobName(String senderJobName) {
		this.senderJobName = senderJobName;
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
