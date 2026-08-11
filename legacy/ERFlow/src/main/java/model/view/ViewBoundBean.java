package model.view;

public class ViewBoundBean {
	private int id;
	private String productId;
	private String productName;
	private String userId;
	private String userName;
	private String postalCode;
	private String address1;
	private String address2;
	private String boundedAt;
	private int count;
	private int type;
	
	public ViewBoundBean() {
		super();
	}

	public ViewBoundBean(int id, String productId, String productName, String userId, String userName, String postalCode,
			String address1, String address2, String boundedAt, int count, int type) {
		super();
		this.id = id;
		this.productId = productId;
		this.productName = productName;
		this.userId = userId;
		this.userName = userName;
		this.postalCode = postalCode;
		this.address1 = address1;
		this.address2 = address2;
		this.boundedAt = boundedAt;
		this.count = count;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	public String getBoundedAt() {
		return boundedAt;
	}

	public void setBoundedAt(String boundedAt) {
		this.boundedAt = boundedAt;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
