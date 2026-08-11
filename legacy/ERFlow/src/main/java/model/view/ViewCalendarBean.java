package model.view;

public class ViewCalendarBean {
	private int id;
	private String userId;
	private int deptId;
	private String deptName;
	private String subject;
	private String content;
	private String start;
	private String end;
	private int type;

	public ViewCalendarBean() {
		super();
	}

	public ViewCalendarBean(int id, String userId, int deptId, String deptName, String subject, String content, String start,
			String end, int type) {
		super();
		this.id = id;
		this.userId = userId;
		this.deptId = deptId;
		this.deptName = deptName;
		this.subject = subject;
		this.content = content;
		this.start = start;
		this.end = end;
		this.type = type;
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

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
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

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
