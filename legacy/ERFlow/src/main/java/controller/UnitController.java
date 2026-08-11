package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.UnitBean;
import model.view.ViewUnitBean;
import service.implementation.UnitServiceImpl;

public class UnitController {
	private final UnitServiceImpl unitSvc;

	public UnitController() {
		unitSvc = new UnitServiceImpl();
	}

	public boolean createUnit(HttpSession session, HttpServletRequest request) {
		return unitSvc.createUnit(session, request);
	}

	public boolean createUnit(HttpSession session, UnitBean unit) {
		return unitSvc.createUnit(session, unit);
	}

	public boolean deleteUnit(HttpSession session, String unitId) {
		return unitSvc.deleteUnit(session, unitId);
	}

	public boolean updateUnit(HttpSession session, HttpServletRequest request) {
		return unitSvc.updateUnit(session, request);
	}

	public boolean updateUnit(HttpSession session, UnitBean unit) {
		return unitSvc.updateUnit(session, unit);
	}
	
	public UnitBean getUnit(HttpSession session, String unitId) {
		return unitSvc.getUnit(session, unitId);
	}
	
	public Vector<ViewUnitBean> getUnits(String searchOption, String searchValue, int start, int cnt) {
		return unitSvc.getUnits(searchOption, searchValue, start, cnt);
	}

	public int getUnitCount(String keyfield, String keyword) {
		return unitSvc.getUnitCount(keyfield, keyword);
	}
}
