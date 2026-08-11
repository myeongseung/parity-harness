package service;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.BoardBean;
import model.DepartmentBean;
import model.JobBean;
import model.ProgramBean;
import model.UserBean;
import model.view.ViewPermissionBean;

public interface PermissionService extends Service {
	boolean changeBoardReadPermission(HttpSession session, BoardBean board, long deptPermission, long jobPermission);
	boolean changeBoardReadPermission(HttpSession session, int boardId, long deptPermission, long jobPermission);
	boolean changeBoardWritePermission(HttpSession session, BoardBean board, long deptPermission, long jobPermission);
	boolean changeBoardWritePermission(HttpSession session, int boardId, long deptPermission, long jobPermission);
	boolean changeDeptPermission(HttpSession session, int deptId, long permission);
	boolean changeDeptPermission(HttpSession session, DepartmentBean dept, long permission);
	boolean changeJobPermission(HttpSession session, int jobId, long permission);
	boolean changeJobPermission(HttpSession session, JobBean job, long permission);
	boolean changeProgramDeptPermission(HttpSession session, String programId, long permission);
	boolean changeProgramJobPermission(HttpSession session, String programId, long permission);
	
	boolean deleteDeptPermission(HttpSession session, int deptId);
	boolean deleteJobPermission(HttpSession session, int jobId);
	
	boolean isAdmin(HttpSession session);
	boolean isAdmin(UserBean user);
	boolean isAdmin(long deptPermission, long jobPermission);
	
	boolean revokeDeptPermission(HttpSession session, int deptId);
	boolean revokeJobPermission(HttpSession session, int jobId);
	
	long getBoardPermission(String category, String flag, int boardId);
	long getBoardReadDeptPermission(int boardId);
	long getBoardReadJobPermission(int boardId);
	long getBoardWriteDeptPermission(int boardId);
	long getBoardWriteJobPermission(int boardId);
	long getDeptPermission(UserBean user);
	long getDeptPermission(int deptId);
	long getJobPermission(UserBean user);
	long getJobPermission(int jobId);
	long getProgramDeptPermission(String programId);
	long getProgramJobPermission(String programId);
	
	// 만약 결과값이 0이면 추가할 수 있는 부서가 없다는 뜻임.
	long next(String flag);
	
	Vector<ViewPermissionBean> getDeptPermissions(String keyfield, String keyword);
	Vector<ViewPermissionBean> getJobPermissions(String keyfield, String keyword);
	Vector<ProgramBean> getProgramPermissions(String keyfield, String keyword);
}
