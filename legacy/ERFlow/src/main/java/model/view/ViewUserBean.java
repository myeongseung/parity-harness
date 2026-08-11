package model.view;

public class ViewUserBean {
	private String id;
	private String name;
	private String socialNumber;
	private String gender;
	private String region;
	private String deptName;
	private String jobName;
	private String postalCode;
	private String address1;
	private String address2;
	private int salary;
	private String extensionPhone;
	private String mobilePhone;
	private String email;
	private String hiredAt;
	
	
	public ViewUserBean() {
		super();
	}

	public ViewUserBean(String id, String name, String socialNumber, String gender, String region, String deptName,
			String jobName, String postalCode, String address1, String address2, int salary, String extensionPhone,
			String mobilePhone, String email, String hiredAt) {
		super();
		this.id = id;
		this.name = name;
		this.socialNumber = socialNumber;
		this.gender = gender;
		this.region = region;
		this.deptName = deptName;
		this.jobName = jobName;
		this.postalCode = postalCode;
		this.address1 = address1;
		this.address2 = address2;
		this.salary = salary;
		this.extensionPhone = extensionPhone;
		this.mobilePhone = mobilePhone;
		this.email = email;
		this.hiredAt = hiredAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSocialNumber() {
		return socialNumber;
	}

	public void setSocialNumber(String socialNumber) {
		this.socialNumber = socialNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
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

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
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
}
