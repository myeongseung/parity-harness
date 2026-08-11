package model;

public class ProgramBean {
	private int id;
	private String programId;
	private String programName;
	private long deptLevel;
	private long jobLevel;
	
	public ProgramBean() {
		super();
	}

	public ProgramBean(int id, String programId, String programName, long deptLevel, long jobLevel) {
		super();
		this.id = id;
		this.programId = programId;
		this.programName = programName;
		this.deptLevel = deptLevel;
		this.jobLevel = jobLevel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public long getDeptLevel() {
		return deptLevel;
	}

	public void setDeptLevel(long deptLevel) {
		this.deptLevel = deptLevel;
	}

	public long getJobLevel() {
		return jobLevel;
	}

	public void setJobLevel(long jobLevel) {
		this.jobLevel = jobLevel;
	}
}
