package service;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.DepartmentBean;

public interface DepartmentService extends Service {
	boolean createDept(HttpSession session, DepartmentBean dept);

	boolean deleteDept(HttpSession session, int deptId);

	boolean updateDept(HttpSession session, DepartmentBean dept);
	
	boolean hasDept(String name);

	DepartmentBean getDept(int deptId);

	// 불러올 때 관리자를 제외하고 불러올 것 (관리자 부서 번호: -1)
	Vector<DepartmentBean> getDepts(String deptName);
}
