package model;

public class TaskHistoryBean {
	private int taskId;
	private String productId;
	private int count;
	
	public TaskHistoryBean() {
		super();
	}

	public TaskHistoryBean(int taskId, String productId, int count) {
		super();
		this.taskId = taskId;
		this.productId = productId;
		this.count = count;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
