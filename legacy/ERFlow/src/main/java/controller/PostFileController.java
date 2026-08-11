package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.FileBean;
import service.implementation.PostFileServiceImpl;

public class PostFileController {
private PostFileServiceImpl postFileSvc = null;
	
	public PostFileController() {
		postFileSvc = new PostFileServiceImpl();
	}
	
	@Deprecated
	public boolean createFile(HttpServletRequest request) {
		return postFileSvc.createFile(request);
	}
	
	public boolean createFile(FileBean bean) {
		return postFileSvc.createFile(bean);
	}
	
	public boolean deleteFiles(int postId) {
		return postFileSvc.deleteFiles(postId);
	}
	
	public String getOriginalFileName(String encoded) {
		return postFileSvc.getOriginalFileName(encoded);
	}
	
	public Vector<FileBean> getFiles(int postId) {
		return postFileSvc.getFiles(postId);
	}
	
	public FileBean getFileByPostId(int postId) {
		return postFileSvc.getFileByPostId(postId);
	}
}
