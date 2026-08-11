package model;

public class UserBean {
	private String id;
	private int jobId;
	private int deptId;
	private String managerId;
	private int salaryId;
	private String name;
	private String password;
	private String socialNumber;
	private String postalCode;
	private String address1;
	private String address2;
	private String extensionPhone;
	private String mobilePhone;
	private String email;
	private String hiredAt;
	private String leftAt;
	
	public UserBean() {
		super();
	}

	public UserBean(String id, int jobId, int deptId, String managerId, int salaryId, String name, String password,
			String socialNumber, String postalCode, String address1, String address2, String extensionPhone,
			String mobilePhone, String email, String hiredAt, String leftAt) {
		super();
		this.id = id;
		this.jobId = jobId;
		this.deptId = deptId;
		this.managerId = managerId;
		this.salaryId = salaryId;
		this.name = name;
		this.password = password;
		this.socialNumber = socialNumber;
		this.postalCode = postalCode;
		this.address1 = address1;
		this.address2 = address2;
		this.extensionPhone = extensionPhone;
		this.mobilePhone = mobilePhone;
		this.email = email;
		this.hiredAt = hiredAt;
		this.leftAt = leftAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public int getSalaryId() {
		return salaryId;
	}

	public void setSalaryId(int salaryId) {
		this.salaryId = salaryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSocialNumber() {
		return socialNumber;
	}

	public void setSocialNumber(String socialNumber) {
		this.socialNumber = socialNumber;
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

	public String getExtensionPhone() {
		return extensionPhone;
	}

	public void setExtensionPhone(String extensionPhone) {
		this.extensionPhone = extensionPhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHiredAt() {
		return hiredAt;
	}

	public void setHiredAt(String hiredAt) {
		this.hiredAt = hiredAt;
	}

	public String getLeftAt() {
		return leftAt;
	}

	public void setLeftAt(String leftAt) {
		this.leftAt = leftAt;
	}
}
