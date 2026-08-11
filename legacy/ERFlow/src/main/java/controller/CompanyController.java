package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.CompanyBean;
import service.implementation.CompanyServiceImpl;

public class CompanyController {
	private final CompanyServiceImpl companySvc;
	
	public CompanyController() {
		companySvc = new CompanyServiceImpl();
	}
	
	public boolean addCompany(HttpSession session, HttpServletRequest request) {
		return companySvc.addCompany(session, request);
	}
	
	public boolean addCompany(HttpSession session, CompanyBean company) {
		return companySvc.addCompany(session, company);
	}
	
	public boolean deleteCompany(HttpSession session, String companyId) {
		return companySvc.deleteCompany(session, companyId);
	}
	
	public boolean updateCompany(HttpSession session, HttpServletRequest request) {
		return companySvc.updateCompany(session, request);
	}
	
	public boolean updateCompany(HttpSession session, CompanyBean company) {
		return companySvc.updateCompany(session, company);
	}
	
	public CompanyBean getCompany(HttpSession session, String companyId) {
		return companySvc.getCompany(session, companyId);
	}
	
	public Vector<CompanyBean> getCompanies() {
		return companySvc.getCompanies();
	}
	
	public Vector<CompanyBean> getCompanies(String searchOption, String searchValue, int subcontract, int start, int cnt) {
		return companySvc.getCompanies(searchOption, searchValue, subcontract, start, cnt);
	}
	
	public int companyCount(String keyfield, String keyword, int subcontract) {
		return companySvc.companyCount(keyfield, keyword, subcontract);
	}
}
