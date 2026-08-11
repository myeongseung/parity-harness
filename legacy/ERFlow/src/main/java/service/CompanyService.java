package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.CompanyBean;

/**
 * CompanyService interface.
 * 
 * @author 장진원
 * */
public interface CompanyService extends Service {
	boolean addCompany(HttpSession session, HttpServletRequest request);

	boolean addCompany(HttpSession session, CompanyBean company);

	boolean deleteCompany(HttpSession session, String companyId);

	boolean updateCompany(HttpSession session, HttpServletRequest request);

	boolean updateCompany(HttpSession session, CompanyBean company);

	int companyCount(String keyfield, String keyword, int subcontract);

	CompanyBean getCompany(HttpSession session, String id);

	Vector<CompanyBean> getCompanies();
	
	Vector<CompanyBean> getCompanies(String keyfield, String keyword, int subcontract, int start, int cnt);
}
