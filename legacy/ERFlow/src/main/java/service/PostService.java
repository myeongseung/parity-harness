package service;

import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.PostBean;
import model.view.ViewPostBean;

/**
 * @author Ki-seok Kang (@jUqIteR)
 * */
public interface PostService extends Service {
	// Add post.	
	int createPost(Map<String, String> params);
	// Delete Post.
	boolean deletePost(int postId);
	// Reply Post.
	boolean replyPost(PostBean bean);
	// Reply PosUp
	boolean replyPosUp(int ref, int pos);
	// Update Post.
	boolean updatePost(Map<String, String> params);
	// Update the count of viewers.
	boolean updateCount(int postId);
	// Get the id of recent added post.
	int getRecentId();
	// Get the total count for searching with keyword by suggesting key field.
	int getTotalCount(int boardId, String keyfield, String keyword);
	// Get the post with post id.
	PostBean getPost(int postId);
	// Get the post view with post id.
	ViewPostBean getPostView(int postId);
	// Get the list of posts.
	Vector<PostBean> getPosts(int boardId);
	
	Vector<PostBean> getPosts(int boardId, String keyfield, String keyword, int start, int cnt);
	
	Vector<ViewPostBean> getPostViews(int boardId, String keyfield, String keyword, int start, int cnt);
}
