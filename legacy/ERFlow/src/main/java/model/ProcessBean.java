package model;

public class ProcessBean {
	private String id;
	private String prevId;
	private String nextId;
	private String name;
	private int priority;
	
	public ProcessBean() {
		super();
	}
	
	public ProcessBean(String id, String prevId, String nextId, String name, int priority) {
		this.id = id;
		this.prevId = prevId;
		this.nextId = nextId;
		this.name = name;
		this.priority = priority;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getPrevId() {
		return prevId;
	}

	public void setPrevId(String prevId) {
		this.prevId = prevId;
	}

	public String getNextId() {
		return nextId;
	}

	public void setNextId(String nextId) {
		this.nextId = nextId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
