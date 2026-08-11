package model;

public class ManagementBean {
	private String processId;
	private String unitId;
	private String startAt;
	private String endedAt;
	private int status;
	
	public ManagementBean() {
		super();
	}
	
	public ManagementBean(String processId, String unitId, String startAt, String endedAt, int statusstatus) {
		this.processId = processId;
		this.unitId = unitId;
		this.startAt = startAt;
		this.endedAt = endedAt;
		this.status = status;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getStartAt() {
		return startAt;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
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
