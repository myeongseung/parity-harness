package model;

public class CalendarBean {
	private int id;
	private String userId;
	private String subject;
	private String content;
	private String startedAt;
	private String endedAt;
	private String createdAt;
	private String updatedAt;
	private int repeats;
	private String repeatOption;
	private String repeatEnd;
	private int type;
	private int active;
	private long permissionDept;
	private long permissionJob;

	public CalendarBean() {
		super();
	}

	public CalendarBean(int id, String userId, String subject, String content, String startedAt, String endedAt,
			String createdAt, String updatedAt, int repeats, String repeatOption, String repeatEnd, int type,
			int active, long permissionDept, long permissionJob) {
		super();
		this.id = id;
		this.userId = userId;
		this.subject = subject;
		this.content = content;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.repeats = repeats;
		this.repeatOption = repeatOption;
		this.repeatEnd = repeatEnd;
		this.type = type;
		this.active = active;
		this.permissionDept = permissionDept;
		this.permissionJob = permissionJob;
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

	public String getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}

	public String getEndedAt() {
		return endedAt;
	}

	public void setEndedAt(String endedAt) {
		this.endedAt = endedAt;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getRepeats() {
		return repeats;
	}

	public void setRepeats(int repeats) {
		this.repeats = repeats;
	}

	public String getRepeatOption() {
		return repeatOption;
	}

	public void setRepeatOption(String repeatOption) {
		this.repeatOption = repeatOption;
	}

	public String getRepeatEnd() {
		return repeatEnd;
	}

	public void setRepeatEnd(String repeatEnd) {
		this.repeatEnd = repeatEnd;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public long getPermissionDept() {
		return permissionDept;
	}

	public void setPermissionDept(long permissionDept) {
		this.permissionDept = permissionDept;
	}

	public long getPermissionJob() {
		return permissionJob;
	}

	public void setPermissionJob(long permissionJob) {
		this.permissionJob = permissionJob;
	}
	
}
