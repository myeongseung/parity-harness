package controller;

import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.ProgramBean;
import service.implementation.ProgramServiceImpl;

public class ProgramController {
	private final PermissionController permissionCon;
	private final ProgramServiceImpl programSvc;
	
	public ProgramController() {
		permissionCon = new PermissionController();
		programSvc = new ProgramServiceImpl();
	}
	
	public int getProgramCount(HttpSession session, String name) {
		int result = 0;
		
		if (permissionCon.isAdmin(session)) {
			result = programSvc.getProgramCount(name); 
		}
		return result;
	}
	
	public ProgramBean getProgram(HttpSession session, int id) {
		ProgramBean bean = null;
		
		if (permissionCon.isAdmin(session)) {
			bean = programSvc.getProgram(id);
		}
		return bean;
	}
	
	public ProgramBean getProgram(HttpSession session, String programName) {
		ProgramBean bean = null;
		
		if (permissionCon.isAdmin(session)) {
			bean = programSvc.getProgram(programName);
		}
		return bean;
	}
	
	public Vector<ProgramBean> getPrograms(HttpSession session, String name, int start, int cnt) {
		Vector<ProgramBean> vlist = null;
		
		if (permissionCon.isAdmin(session)) {
			vlist = programSvc.getPrograms(name, start, cnt);
		}
		return vlist;
	}
}
