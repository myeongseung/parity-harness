package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.WebHelper;
import model.CommentBean;
import model.UserBean;
import model.view.ViewCommentBean;
import model.view.ViewPostBean;
import service.implementation.CommentServiceImpl;

public class CommentController {
	private final CommentServiceImpl commentSvc;
	private final PermissionController permissionCon;
	private final PostController postCon;
	
	public CommentController() {
		commentSvc = new CommentServiceImpl();
		permissionCon = new PermissionController();
		postCon = new PostController();
	}
	
	public boolean createComment(HttpSession session, CommentBean comment) {
		return commentSvc.createComment(comment);
	}

	public boolean createComment(HttpSession session, HttpServletRequest request) {
		boolean result = false;
		
		if (session != null && request != null) {
			if (WebHelper.isLogin(session)) {
				result = commentSvc.createComment(request);
			}
		}
		return result;
	}
	
	public boolean deleteComment(HttpSession session, int commentId) {
		boolean result = false;
		UserBean user = WebHelper.getValidUser(session);
		CommentBean originalComment = commentSvc.getComment(commentId);
		
		if (user != null && originalComment != null) {
			if (permissionCon.isAdmin(session) ||
					user.getId().equals(originalComment.getUserId())) {
				result = commentSvc.deleteComment(commentId);
			}
		}
		return result;
	}
	
	public boolean deleteAllComments(HttpSession session, int boardId, int postId) {
		boolean result = false;
		UserBean user = WebHelper.getValidUser(session);
		ViewPostBean originalPost = postCon.getPostView(session, boardId, postId);
		
		if (user != null && originalPost != null) {
			if (permissionCon.isAdmin(session) ||
					user.getId().equals(originalPost.getUserId())) {
				result = commentSvc.deleteAllComments(postId);
			}
		}
		return result;
	}
	
	public boolean updateComment(HttpSession session, CommentBean comment) {
		return commentSvc.updateComment(comment);
	}
	
	public CommentBean getComment(int commentId) {
		return commentSvc.getComment(commentId);
	}
	
	public Vector<CommentBean> getComments(int postId){
		return commentSvc.getComments(postId);
	}
	
	public boolean createReply(HttpSession session, CommentBean comment) {
		return commentSvc.createReply(comment);
	}
	
	public int getTotalCommentCount(int postId) {
		return commentSvc.getTotalCommentCount(postId);
	}
	
	public Vector<ViewCommentBean> getCommentViews(int postId) {
		return commentSvc.getCommentViews(postId);
	}
}
