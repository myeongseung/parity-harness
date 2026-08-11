package model;

public class ProductBean {
	private String id;
	private String name;
	private int count;
	private int type;
	
	public ProductBean() {
		super();
	}
	
	public ProductBean(String id, String name, int count, int type) {
		this.id = id;
		this.name = name;
		this.count = count;
		this.type = type;
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
