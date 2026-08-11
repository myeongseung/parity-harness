package model;

public class TemplateBean {
	private int id;
	private String subject;
	private String content;
	
	public TemplateBean() {
		super();
	}

	public TemplateBean(int id, String subject, String content) {
		super();
		this.id = id;
		this.subject = subject;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
