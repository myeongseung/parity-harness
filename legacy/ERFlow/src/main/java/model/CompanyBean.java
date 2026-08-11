package model;

public class CompanyBean {
	private int id;
	private String name;
	private String postalCode;
	private String address1;
	private String address2;
	private String phone;
	private String businessCode;
	private String field;
	private String bankCode;
	private String bankAccount;
	private int subcontract;

	public CompanyBean() {
		super();
	}
	
	public CompanyBean(int id, String name, String postalCode, String address1,
			String address2, String phone, String businessCode, String field, String bankCode, String bankAccount,
			int subcontract ) {
		this.id = id;
		this.name = name;
		this.postalCode = postalCode;
		this.address1 = address1;
		this.address2 = address2;
		this.phone = phone;
		this.businessCode = businessCode;
		this.field = field;
		this.bankCode = bankCode;
		this.bankAccount = bankAccount;
		this.subcontract = subcontract;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public int getSubcontract() {
		return subcontract;
	}

	public void setSubcontract(int subcontract) {
		this.subcontract = subcontract;
	}
}
