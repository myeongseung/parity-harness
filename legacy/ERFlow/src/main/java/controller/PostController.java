package controller;

import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.WebHelper;
import model.BoardBean;
import model.PostBean;
import model.UserBean;
import model.view.ViewPostBean;
import service.implementation.CommentServiceImpl;
import service.implementation.PostServiceImpl;

public class PostController {
	private final CommentServiceImpl commentSvc;
	private final PostServiceImpl postSvc;
	
	private final PermissionController permissionCon;
	
	public PostController() {
		commentSvc = new CommentServiceImpl();
		postSvc = new PostServiceImpl();
		
		permissionCon = new PermissionController();
	}
		
	public int createPost(HttpSession session, Map<String, String> params) {
		int result = -1;
		
		if (session != null && params != null) {
			int boardId = Integer.parseInt(params.get("boardId"));
			boolean hasPermission = permissionCon
					.hasBoardWritePermission(session, boardId);
			
			// 게시글을 작성할 권한이 있다면,
			if (hasPermission) {
				result = postSvc.createPost(params);
			}
		}
		return result;
	}
	
	public boolean deletePost(HttpSession session, int boardId, int postId) {
		boolean result = false;
		
		if (session != null) {
			UserBean user = WebHelper.getValidUser(session);
			PostBean post = postSvc.getPost(postId);
			
			// 만약 관리자 권한이 없다면, 일반 유저이므로, 자기 자신만 삭제할 수 있도록 함. 
			// 세션이 유지되고 있는 상태에서 작성자 아이디랑 현재 세션의 아이디가 같다면,
			if (user != null && (permissionCon.isAdmin(session) ||
					user.getId().equals(post.getUserId()))) {
				result = postSvc.deletePost(postId);
			}
		}
		return result;
	}
		
	public boolean updatePost(HttpSession session, Map<String, String> params) {
		boolean result = false;
		int postId = -1;
		
		if (session != null && params != null) {
			if (params.containsKey("id")) {
				postId = Integer.parseInt(params.get("id"));				
			}
			UserBean user = WebHelper.getValidUser(session);
			PostBean post = postSvc.getPost(postId);
			
			// 작성자가 같은 사람만 수정할 수 있음
			if (user != null && post != null &&
				user.getId().equals(post.getUserId())) {
				result = postSvc.updatePost(params);
			}
		}
		return result;
	}
	
	public boolean replyPost(PostBean bean) {
		return postSvc.replyPost(bean);
	}

	public boolean replyPosUp(int refId, int pos){
		return postSvc.replyPosUp(refId, pos);
	}
	
	public boolean updateCount(int postId) {
		return postSvc.updateCount(postId);
	}
	
	public int getTotalCount(int boardId, String keyfield, String keyword) {
		return postSvc.getTotalCount(boardId, keyfield, keyword);
	}
	
	public int getTotalCommentCount(int postId) {
		return commentSvc.getTotalCommentCount(postId);
	}
	
	public ViewPostBean getPostView(HttpSession session, int boardId, int postId) {
		ViewPostBean post = null;
		
		if (session != null) {
			boolean hasPermission = permissionCon.hasBoardReadPermission(session, boardId);
			
			// 게시글을 읽을 권한이 있다면,
			if (hasPermission) {
				post = postSvc.getPostView(postId);
			}
		}
		return post;
	}
	
	public Vector<PostBean> getPosts(int boardId) {
		return postSvc.getPosts(boardId);
	}
	
	public Vector<PostBean> getPosts(int boardId, String keyfield, String keyword, int start, int cnt) {
		return postSvc.getPosts(boardId, keyfield, keyword, start, cnt);
	}
	
	public Vector<ViewPostBean> getPostViews(int boardId, String keyfield, String keyword, int start, int cnt) {
		return postSvc.getPostViews(boardId, keyfield, keyword, start, cnt);
	}
}
