package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.FileBean;

public interface PostFileService extends Service {
	boolean createFile(HttpServletRequest request);
	@Deprecated
	boolean createFile(Vector<FileBean> files, int parentPostId);
	
	boolean createFile(FileBean file);

	boolean deleteFiles(int postId);
	boolean updateFile(HttpServletRequest reqeust);
	
	String getOriginalFileName(String encoded);
	
	Vector<FileBean> getFiles(int postId);
	FileBean getFileByPostId(int postId);
}
