package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.UnitBean;
import model.view.ViewUnitBean;

public interface UnitService extends Service {
	boolean createUnit(HttpSession session, HttpServletRequest request);
	boolean createUnit(HttpSession session, UnitBean unit);
	boolean deleteUnit(HttpSession session, String unitId);
	boolean updateUnit(HttpSession session, HttpServletRequest request);
	boolean updateUnit(HttpSession session, UnitBean unit);
	
	Vector<ViewUnitBean> getUnits(String searchOption, String searchValue , int start, int cnt);
	
	int getUnitCount(String keyfield, String keyword);
	
	UnitBean getUnit(HttpSession session, String unitId);
}
