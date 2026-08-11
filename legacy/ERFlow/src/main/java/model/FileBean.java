package model;

public class FileBean {
	private int id;
	private int refId;
	private String originalName;
	private String name;
	private String extension;
	private long size;
		
	public FileBean() {
		super();
	}

	public FileBean(int id, int refId, String originalName, String name, String extension, long size) {
		super();
		this.id = id;
		this.refId = refId;
		this.originalName = originalName;
		this.name = name;
		this.extension = extension;
		this.size = size;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
