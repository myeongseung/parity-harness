package model.view;

public class ViewWorkBean {
	private int id;
	private String userId;
	private String userName;
	private String deptName;
	private String jobName;
	private String startedAt;
	private String endedAt;
	private int status;
	
	public ViewWorkBean() {
		super();
	}

	public ViewWorkBean(int id, String userId, String userName, String deptName, String jobName, String startedAt, String endedAt,
			int status) {
		super();
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.deptName = deptName;
		this.jobName = jobName;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
		this.status = status;
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

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
