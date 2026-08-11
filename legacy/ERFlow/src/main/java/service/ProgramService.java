package service;

import java.util.Vector;

import model.ProgramBean;

public interface ProgramService extends Service {	
	int getProgramCount(String name);
	
	ProgramBean getProgram(int id);
	
	ProgramBean getProgram(String programName);
	
	Vector<ProgramBean> getPrograms(String name, int start, int cnt);
}
