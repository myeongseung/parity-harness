package model;

public class DepartmentBean {
	private int id;
	private String managerId;
	private String postalCode;
	private String address1;
	private String address2;
	private String name;
	
	public DepartmentBean() {
		super();
	}

	public DepartmentBean(int id, String managerId, String postalCode, 
			String address1, String address2, String name) {
		super();
		this.id = id;
		this.managerId = managerId;
		this.postalCode = postalCode;
		this.address1 = address1;
		this.address2 = address2;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
