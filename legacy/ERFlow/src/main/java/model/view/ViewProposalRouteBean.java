package model.view;

import java.util.Vector;

public class ViewProposalRouteBean {
	private int id;
	private String userId;
	private String userName;
	private String jobName;
	private String deptName;
	private String nickname;
	private Vector<ViewUserBean> route;
	private int type;
	private String createdAt;
	
	public ViewProposalRouteBean() {
		super();
	}

	public ViewProposalRouteBean(int id, String userId, String userName, String jobName, String deptName,
			String nickname, Vector<ViewUserBean> route, int type, String createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.jobName = jobName;
		this.deptName = deptName;
		this.nickname = nickname;
		this.route = route;
		this.type = type;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Vector<ViewUserBean> getRoute() {
		return route;
	}

	public void setRoute(Vector<ViewUserBean> route) {
		this.route = route;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}
