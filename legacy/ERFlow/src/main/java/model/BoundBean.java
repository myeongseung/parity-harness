package model;

public class BoundBean {
	private int id;
	private String productId;
	private String userId;	
	private String postalCode;
	private String address1;
	private String address2;
	private String boundedAt;
	private int count;
	private int type;
	
	public BoundBean() {
		super();
	}
	
	public BoundBean(int id, String productId, String postalCode, String userId, String address1,
			String address2, String boundedAt, int count, int type) {
		this.id = id;
		this.productId = productId;
		this.userId = userId;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
