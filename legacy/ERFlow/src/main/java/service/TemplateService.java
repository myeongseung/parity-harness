package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.TemplateBean;

public interface TemplateService extends Service {
	boolean createTemplate(HttpServletRequest request);

	boolean createTemplate(TemplateBean bean);
	
	boolean deleteTemplate(int templateId);
	
	boolean updateTemplate(HttpServletRequest request);
	
	boolean updateTemplate(TemplateBean bean);
	
	TemplateBean getTemplate(int templateId);
	
	Vector<TemplateBean> getTemplates();
}
