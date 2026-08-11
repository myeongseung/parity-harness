package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.TemplateBean;
import service.implementation.TemplateServiceImpl;

public class TemplateController {
	private final PermissionController permissionCon;
	private final TemplateServiceImpl templateSvc;
	
	public TemplateController() {
		permissionCon = new PermissionController();
		templateSvc = new TemplateServiceImpl();
	}
	
	public boolean createTemplate(HttpSession session, HttpServletRequest request) {
		boolean result = false;
		
		if (session != null && request != null) {
			if (permissionCon.isAdmin(session)) {
				result = templateSvc.createTemplate(request);
			}
		}
		return result;
	}
	
	public boolean createTemplate(HttpSession session, TemplateBean bean) {
		boolean result = false;
		
		if (session != null && bean != null) {
			if (permissionCon.isAdmin(session)) {
				result = templateSvc.createTemplate(bean);
			}
		}
		return result;
	}
	
	public boolean deleteTemplate(HttpSession session, int templateId) {
		boolean result = false;
		
		if (session != null) {
			if (permissionCon.isAdmin(session)) {
				result = templateSvc.deleteTemplate(templateId);
			}
		}
		return result;
	}
	
	public boolean updateTemplate(HttpSession session, HttpServletRequest request) {
		boolean result = false;
		
		if (session != null && request != null) {
			if (permissionCon.isAdmin(session)) {
				result = templateSvc.updateTemplate(request);
			}
		}
		return result;
	}
	
	public boolean updateTemplate(HttpSession session, TemplateBean bean) {
		boolean result = false;
		
		if (session != null && bean != null) {
			if (permissionCon.isAdmin(session)) {
				result = templateSvc.updateTemplate(bean);
			}
		}
		return result;
	}
	
	public TemplateBean getTemplate(int templateId) {
		return templateSvc.getTemplate(templateId);
	}
	
	public Vector<TemplateBean> getTemplates() {
		return templateSvc.getTemplates();
	}
}
