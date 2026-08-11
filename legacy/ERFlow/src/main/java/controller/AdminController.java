package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.WebHelper;
import model.BoardBean;
import model.DepartmentBean;
import model.JobBean;
import model.ProgramBean;
import model.TemplateBean;
import model.UserBean;
import model.view.ViewPermissionBean;
import model.view.ViewPostBean;
import model.view.ViewUserBean;

public class AdminController {
	private final BoardController boardCon;
	private final DepartmentController deptCon;
	private final JobController jobCon;
	private final PermissionController permissionCon;
	private final PostController postCon;
	private final ProgramController programCon;
	private final TemplateController templateCon;
	private final UserController userCon;
	
	public AdminController() {
		boardCon = new BoardController();
		deptCon = new DepartmentController();
		jobCon = new JobController();
		permissionCon = new PermissionController();
		postCon = new PostController();
		programCon = new ProgramController();
		templateCon = new TemplateController();
		userCon = new UserController();
	}
	
	// BoardController Bean
	public boolean createBoard(HttpSession session, HttpServletRequest request) {
		return boardCon.createBoard(session, request);
	}
	
	public boolean deleteBoard(HttpSession session, int boardId) {
		return boardCon.deleteBoard(session, boardId);
	}
	
	public boolean hasBoardName(String name) {
		return boardCon.hasBoardName(name);
	}
	
	public boolean updateBoard(HttpSession session, HttpServletRequest request) {
		return boardCon.updateBoard(session, request);
	}
	
	public int getBoardCounts(String keyword) {
		return boardCon.getBoardCount(keyword);
	}
	
	public BoardBean getBoard(int boardId) {
		return boardCon.getBoard(boardId);
	}
	
	public Vector<BoardBean> getBoards(String keyword, int start, int cnt) {
		return boardCon.getBoards(keyword, start, cnt);
	}
	
	// UserController Area
	
	public void logout(HttpSession session) {
		userCon.logout(session);
	}
	
	public boolean changePassword(String userId, String password) {
		return userCon.changePassword(userId, password);
	}
	
	public boolean deleteUser(HttpSession session, String userId) {
		return userCon.deleteUser(session, userId);
	}
	
	public boolean register(HttpSession session, UserBean bean) {
		return userCon.register(session, bean);
	}
	
	public boolean updateUser(HttpSession session, UserBean bean) {
		return userCon.updateUser(session, bean);
	}
	
	public int getUserTotalCount(String keyfield, String keyword) {
		return userCon.getUserTotalCount(keyfield, keyword);
	}
	
	public UserBean getUser(String id) {
		return userCon.getUser(id);
	}
	
	public ViewUserBean getUserView(String id) {
		return userCon.getUserView(id);
	}
	
	public Vector<UserBean> getUsers() {
		return userCon.getUsers();
	}
	
	public Vector<ViewUserBean> getUserViews(String keyfield, String keyword, int start, int cnt) {
		return userCon.getUserViews(keyfield, keyword, start, cnt);
	}
	
	
	
	// DepartmentController Area
	
	public boolean createDept(HttpSession session, DepartmentBean bean) {
		return deptCon.createDept(session, bean);
	}
	
	public boolean deleteDept(HttpSession session, int deptId) {
		return deptCon.deleteDept(session, deptId);
	}
	
	public boolean updateDept(HttpSession session, DepartmentBean bean) {
		return deptCon.updateDept(session, bean);
	}
	
	public boolean hasDept(String name) {
		return deptCon.hasDept(name);
	}
	
	public DepartmentBean getDept(int deptId) {
		return deptCon.getDept(deptId);
	}
	
	public Vector<DepartmentBean> getDepts(String deptName) {
		return deptCon.getDepts(deptName);
	}
	
	
	
	// JobController Bean
	
	public boolean createJob(HttpSession session, JobBean bean) {
		return jobCon.createJob(session, bean);
	}
	
	public boolean deleteJob(HttpSession session, int jobId) {
		return jobCon.deleteJob(session, jobId);
	}
	
	public boolean updateJob(HttpSession session, JobBean bean) {
		return jobCon.updateJob(session, bean);
	}
	
	public boolean hasJob(String name) {
		return jobCon.hasJob(name);
	}
	
	public JobBean getJob(int jobId) {
		return jobCon.getJob(jobId);
	}
	
	public Vector<JobBean> getJobs(String jobName) {
		return jobCon.getJobs(jobName);
	}
	
	// ProgramController Bean
	
	public int getProgramCount(HttpSession session, String name) {
		int result = 0;
		
		if (permissionCon.isAdmin(session)) {
			result = programCon.getProgramCount(session, name); 
		}
		return result;
	}
	
	public ProgramBean getProgram(HttpSession session, int id) {
		ProgramBean bean = null;
		
		if (permissionCon.isAdmin(session)) {
			bean = programCon.getProgram(session, id);
		}
		return bean;
	}
	
	public ProgramBean getProgram(HttpSession session, String programName) {
		ProgramBean bean = null;
		
		if (permissionCon.isAdmin(session)) {
			bean = programCon.getProgram(session, programName);
		}
		return bean;
	}
	
	public Vector<ProgramBean> getPrograms(HttpSession session, String name, int start, int cnt) {
		Vector<ProgramBean> vlist = null;
		
		if (permissionCon.isAdmin(session)) {
			vlist = programCon.getPrograms(session, name, start, cnt);
		}
		return vlist;
	}
	
	// PermissionController Bean
	public boolean changeBoardReadPermission(HttpSession session, HttpServletRequest request, long deptPermission, long jobPermission) {
		BoardBean board = null;
		
		if (request != null) {			
			if (request.getParameter("id") != null) {
				board = new BoardBean();
				board.setId(WebHelper.parseInt(request, "id"));				
			}
		}
		return permissionCon.changeBoardReadPermission(session, board, deptPermission, jobPermission);
	}
	
	public boolean changeBoardWritePermission(HttpSession session, HttpServletRequest request, long deptPermission, long jobPermission) {
		BoardBean board = null;
		
		if (request != null) {			
			if (request.getParameter("id") != null) {
				board = new BoardBean();
				board.setId(WebHelper.parseInt(request, "id"));				
			}
		}
		return permissionCon.changeBoardWritePermission(session, board, deptPermission, jobPermission);
	}
	
	public boolean changeDeptPermission(HttpSession session, DepartmentBean dept, long permission) {
		return permissionCon.changeDeptPermission(session, dept, permission);
	}
	
	public boolean changeJobPermission(HttpSession session, JobBean job, long permission) {
		return permissionCon.changeJobPermission(session, job, permission);
	}
	
	public boolean deleteDeptPermission(HttpSession session, int deptId) {
		return permissionCon.deleteDeptPermission(session, deptId);
	}
	
	public boolean deleteJobPermission(HttpSession session, int jobId) {
		return permissionCon.deleteJobPermission(session, jobId);
	}
	
	public Vector<ViewPermissionBean> getDeptPermissions(String keyfield, String keyword) {
		return permissionCon.getDeptPermissions(keyfield, keyword);
	}
	
	public Vector<ViewPermissionBean> getJobPermissions(String keyfield, String keyword) {
		return permissionCon.getJobPermissions(keyfield, keyword);
	}
	
	public Vector<ProgramBean> getProgramPermissions(String keyfield, String keyword) {
		return permissionCon.getProgramPermissions(keyfield, keyword);
	}
	
	// PostController
	
	public int getTotalCount(int boardId, String keyfield, String keyword) {
		return postCon.getTotalCount(boardId, keyfield, keyword);
	}
	
	public int getTotalCommentCount(int postId) {
		return postCon.getTotalCommentCount(postId);
	}
	
	public Vector<ViewPostBean> getPostViews(int boardId, String keyfield, String keyword, int start, int cnt) {
		return postCon.getPostViews(boardId, keyfield, keyword, start, cnt);
	}
	
	// TemplateController
	
	public boolean createTemplate(HttpSession session, HttpServletRequest request) {
		return templateCon.createTemplate(session, request);
	}
	
	public boolean createTemplate(HttpSession session, TemplateBean bean) {
		return templateCon.createTemplate(session, bean);
	}
	
	public boolean deleteTemplate(HttpSession session, int templateId) {
		return templateCon.deleteTemplate(session, templateId);
	}
	
	public boolean updateTemplate(HttpSession session, HttpServletRequest request) {
		return templateCon.updateTemplate(session, request);
	}
	
	public boolean updateTemplate(HttpSession session, TemplateBean bean) {
		return templateCon.updateTemplate(session, bean);
	}
	
	public TemplateBean getTemplate(int templateId) {
		return templateCon.getTemplate(templateId);
	}
	
	public Vector<TemplateBean> getTemplates() {
		return templateCon.getTemplates();
	}
}
