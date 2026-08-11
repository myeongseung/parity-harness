package model.view;

public class ViewPermissionBean {
	private int id;
	private int classId;
	private String className;
	private long level;
	private long permission;
	
	public ViewPermissionBean() {
		super();
	}

	public ViewPermissionBean(int id, int classId, String className, long level, long permission) {
		super();
		this.id = id;
		this.classId = classId;
		this.className = className;
		this.level = level;
		this.permission = permission;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getLevel() {
		return level;
	}

	public void setLevel(long level) {
		this.level = level;
	}

	public long getPermission() {
		return permission;
	}

	public void setPermission(long permission) {
		this.permission = permission;
	}
}
