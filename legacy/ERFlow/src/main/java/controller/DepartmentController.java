package controller;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.DepartmentBean;
import service.implementation.DepartmentServiceImpl;

public class DepartmentController {
	private final DepartmentServiceImpl deptSvc;
	
	public DepartmentController() {
		deptSvc = new DepartmentServiceImpl();
	}
	
	public boolean createDept(HttpSession session, DepartmentBean dept) {
		return deptSvc.createDept(session, dept);
	}

	public boolean deleteDept(HttpSession session, int deptId) {
		return deptSvc.deleteDept(session, deptId);
	}
	
	public boolean updateDept(HttpSession session, DepartmentBean dept) {
		return deptSvc.updateDept(session, dept);
	}
	
	public boolean hasDept(String name) {
		return deptSvc.hasDept(name);
	}
	
	public DepartmentBean getDept(int deptId) {
		return deptSvc.getDept(deptId);
	}
	
	public Vector<DepartmentBean> getDepts(String deptName) {
		return deptSvc.getDepts(deptName);
	}
}
