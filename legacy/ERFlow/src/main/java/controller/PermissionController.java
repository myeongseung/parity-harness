package controller;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.BoardBean;
import model.DepartmentBean;
import model.JobBean;
import model.ProgramBean;
import model.UserBean;
import model.view.ViewPermissionBean;
import service.implementation.PermissionServiceImpl;

public class PermissionController {
	private final PermissionServiceImpl permissionSvc;
	
	public PermissionController() {
		permissionSvc = new PermissionServiceImpl();
	}
	
	public boolean changeBoardReadPermission(HttpSession session, BoardBean board, long deptPermission, long jobPermission) {
		return permissionSvc.changeBoardReadPermission(session, board, deptPermission, jobPermission);
	}
	
	public boolean changeBoardWritePermission(HttpSession session, BoardBean board, long deptPermission, long jobPermission) {
		return permissionSvc.changeBoardWritePermission(session, board, deptPermission, jobPermission);
	}
	
	public boolean changeDeptPermission(HttpSession session, DepartmentBean dept, long permission) {
		return permissionSvc.changeDeptPermission(session, dept, permission);
	}

	public boolean changeJobPermission(HttpSession session, JobBean job, long permission) {
		return permissionSvc.changeJobPermission(session, job, permission);
	}
	
	public boolean changeProgramDeptPermission(HttpSession session, String programId, long permission) {
		return permissionSvc.changeProgramDeptPermission(session, programId, permission);
	}	
	
	public boolean changeProgramJobPermission(HttpSession session, String programId, long permission) {
		return permissionSvc.changeProgramJobPermission(session, programId, permission);
	}
	
	public boolean deleteDeptPermission(HttpSession session, int deptId) {
		return permissionSvc.deleteDeptPermission(session, deptId);
	}

	public boolean deleteJobPermission(HttpSession session, int jobId) {
		return permissionSvc.deleteJobPermission(session, jobId);
	}
	
	public boolean isAdmin(HttpSession session) {
		return permissionSvc.isAdmin(session);
	}
	
	public boolean hasBoardReadPermission(HttpSession session, int boardId) {
		boolean result = false;
		long boardDeptPermission = 0L;
		long boardJobPermission = 0L;
		long userDeptPermission = 0L;
		long userJobPermission = 0L;
		
		if (session != null) {
			UserBean user = (UserBean)session.getAttribute("user");
			
			if (user != null) {
				userDeptPermission = permissionSvc.getDeptPermission(user);
				userJobPermission = permissionSvc.getJobPermission(user);
			}
		}
		if (boardId > 0) {
			boardDeptPermission = permissionSvc.getBoardReadDeptPermission(boardId);
			boardJobPermission = permissionSvc.getBoardReadJobPermission(boardId);
		}
		result = (boardDeptPermission & userDeptPermission) != 0 &&
				(boardJobPermission & userJobPermission) != 0;
		return result;
	}
	
	public boolean hasBoardWritePermission(HttpSession session, int boardId) {
		boolean result = false;
		long boardDeptPermission = 0L;
		long boardJobPermission = 0L;
		long userDeptPermission = 0L;
		long userJobPermission = 0L;
		
		if (session != null) {
			UserBean user = (UserBean)session.getAttribute("user");
			
			if (user != null) {
				userDeptPermission = permissionSvc.getDeptPermission(user);
				userJobPermission = permissionSvc.getJobPermission(user);
			}
		}
		if (boardId > 0) {
			boardDeptPermission = permissionSvc.getBoardWriteDeptPermission(boardId);
			boardJobPermission = permissionSvc.getBoardWriteJobPermission(boardId);
		}
		result = (boardDeptPermission & userDeptPermission) != 0 &&
				(boardJobPermission & userJobPermission) != 0;
		return result;
	}
	
	public boolean hasUserPermission(HttpSession session, String flag, long required)
			throws IllegalArgumentException {
		boolean result = false;
		
		switch (flag) {
			case "dept" -> {
				result = hasUserDeptPermission(session, required);
			}
			case "job" -> {
				result = hasUserJobPermission(session, required);
			}
			default -> {
				throw new IllegalArgumentException();
			}
		}
		return result;
	}
	
	public boolean hasProgramPermission(HttpSession session, String programId) {
		boolean result = false;
		long programDeptPermission = 0L;
		long programJobPermission = 0L;
		long userDeptPermission = 0L;
		long userJobPermission = 0L;
		
		if (session != null) {
			UserBean user = (UserBean)session.getAttribute("user");
			
			if (user != null) {
				userDeptPermission = permissionSvc.getDeptPermission(user);
				userJobPermission = permissionSvc.getJobPermission(user);
			}
		}
		if (programId != null) {
			programDeptPermission = permissionSvc.getProgramDeptPermission(programId);
			programJobPermission = permissionSvc.getProgramJobPermission(programId);
		}
		result = (programDeptPermission & userDeptPermission) != 0 &&
				(programJobPermission & userJobPermission) != 0;
		return result;
	}
	/*
	 * 부서의 권한을 사용자가 가지고 있는지 확인하는 메소드
	 * 
	 * @parameter
	 * 		session: 현재 로그인된 사용자
	 * 		required: 요구 권한 
	 * */
	public boolean hasUserDeptPermission(HttpSession session, long required) {
		boolean result = false;
		
		if (session != null) {
			UserBean user = (UserBean)session.getAttribute("user");
			long userPermission = permissionSvc.getDeptPermission(user);
			
			// Using bitwise AND operator
			result = (userPermission & required) != 0;
		}
		return result;
	}
	
	/*
	 * 직급의 권한을 사용자가 가지고 있는지 확인하는 메소드
	 * 
	 * @parameter
	 * 		session: 현재 로그인된 사용자
	 * 		required: 요구 권한 
	 * */
	public boolean hasUserJobPermission(HttpSession session, long required) {
		boolean result = false;
		
		if (session != null) {
			UserBean user = (UserBean)session.getAttribute("user");
			long userPermission = permissionSvc.getJobPermission(user);
			
			// Using bitwise AND operator
			result = (userPermission & required) != 0;
		}
		return result;
	}
	
	/*
	 * 직급의 권한을 사용자가 가지고 있는지 확인하는 메소드
	 * 
	 * @parameter
	 * 		session: 현재 로그인된 사용자
	 * 		flag: "dept", "job" 등을 구분하는 변수.
	 * 			  그 외에는 오류가 발생하니 유의할 것.
	 * 		id: 부서 아이디 or 직급 아이디.
	 * */
	public boolean revokePermission(HttpSession session, String flag, int id)
			throws IllegalArgumentException {
		boolean result = false;
		
		switch (flag) {
			case "dept" -> {
				result = permissionSvc.revokeDeptPermission(session, id);
			}
			case "job" -> {
				result = permissionSvc.revokeJobPermission(session, id);
			}
			default -> {
				throw new IllegalArgumentException();
			}
		}
		return result;
	}
	
	/*
	 * 다음 삽입할 직급의 권한 수준을 불러오는 메소드.
	 * 
	 * @parameter
	 * 		flag: "dept", "job" 등을 구분하는 변수.
	 * 			  그 외에는 오류가 발생하니 유의할 것.
	 * */
	public long next(String flag) {
		return permissionSvc.next(flag);
	}
	
	public Vector<ViewPermissionBean> getDeptPermissions(String keyfield, String keyword) {
		return permissionSvc.getDeptPermissions(keyfield, keyword);
	}
	
	public Vector<ViewPermissionBean> getJobPermissions(String keyfield, String keyword) {
		return permissionSvc.getJobPermissions(keyfield, keyword);
	}
	
	public Vector<ProgramBean> getProgramPermissions(String keyfield, String keyword) {
		return permissionSvc.getProgramPermissions(keyfield, keyword);
	}
}
