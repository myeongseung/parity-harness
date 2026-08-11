package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.CommentBean;
import model.view.ViewCommentBean;

public interface CommentService extends Service {
	boolean createComment(CommentBean comment);

	boolean createComment(HttpServletRequest request);

	boolean createReply(CommentBean comment);

	boolean deleteAllComments(int postId);

	boolean deleteComment(int commentId);

	boolean updateComment(CommentBean comment);

	boolean hasReply(int commentId);
	
	int getTotalCommentCount(int postId);

	CommentBean getComment(int commentId);

	Vector<CommentBean> getComments(int postId);
	
	Vector<ViewCommentBean> getCommentViews(int postId);
}
