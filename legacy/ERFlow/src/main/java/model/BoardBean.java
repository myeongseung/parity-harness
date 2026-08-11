package model;

public class BoardBean {
	private int id;
	private String subject;
	private String createAt;
	private long deptReadPermissionLevel;
	private long jobReadPermissionLevel;
	private long deptWritePermissionLevel;
	private long jobWritePermissionLevel;
	
	public BoardBean() {
		super();
	}

	public BoardBean(int id, String subject, String createAt, long deptReadPermissionLevel, long jobReadPermissionLevel,
			long deptWritePermissionLevel, long jobWritePermissionLevel) {
		super();
		this.id = id;
		this.subject = subject;
		this.createAt = createAt;
		this.deptReadPermissionLevel = deptReadPermissionLevel;
		this.jobReadPermissionLevel = jobReadPermissionLevel;
		this.deptWritePermissionLevel = deptWritePermissionLevel;
		this.jobWritePermissionLevel = jobWritePermissionLevel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

	public long getDeptReadPermissionLevel() {
		return deptReadPermissionLevel;
	}

	public void setDeptReadPermissionLevel(long deptReadPermissionLevel) {
		this.deptReadPermissionLevel = deptReadPermissionLevel;
	}

	public long getJobReadPermissionLevel() {
		return jobReadPermissionLevel;
	}

	public void setJobReadPermissionLevel(long jobReadPermissionLevel) {
		this.jobReadPermissionLevel = jobReadPermissionLevel;
	}

	public long getDeptWritePermissionLevel() {
		return deptWritePermissionLevel;
	}

	public void setDeptWritePermissionLevel(long deptWritePermissionLevel) {
		this.deptWritePermissionLevel = deptWritePermissionLevel;
	}

	public long getJobWritePermissionLevel() {
		return jobWritePermissionLevel;
	}

	public void setJobWritePermissionLevel(long jobWritePermissionLevel) {
		this.jobWritePermissionLevel = jobWritePermissionLevel;
	}
}
