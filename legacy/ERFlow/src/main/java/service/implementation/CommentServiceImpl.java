package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import helper.ResultSetExtractHelper;
import model.CommentBean;
import model.view.ViewCommentBean;
import service.CommentService;

public class CommentServiceImpl implements CommentService {
	private final DBConnectionServiceImpl pool;
	
	public CommentServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}
	
	@Override
	public boolean createComment(CommentBean comment) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if(comment != null) {		
			try {
				con = pool.getConnection();
				String sql = "insert into comment_tbl values (0, ?, 0, ?, ?, 0, now())";
				pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				
				pstmt.setInt(1, comment.getPostId());
				pstmt.setString(2, comment.getUserId());
				pstmt.setString(3, comment.getComment());
				
				flag = pstmt.executeUpdate() == 1;
				
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						comment.setId(generatedKeys.getInt(1));
						comment.setRefId(generatedKeys.getInt(1));
		            } else {
		                throw new SQLException("Creating user failed, no ID obtained.");
		            }
				}
				pstmt.close();
				sql = "update comment_tbl set comment_tbl_ref_id = ? where id = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, comment.getRefId());
				pstmt.setInt(2, comment.getId());
				
				flag = pstmt.executeUpdate() == 1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				pool.freeConnection(con, pstmt);
			}
		}
		return flag;
	}
	
	@Override
	public boolean createComment(HttpServletRequest request) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			int postId = Integer.parseInt(request.getParameter("postId"));
			String userId = request.getParameter("userId");
			String comment = request.getParameter("comment");
			int generatedCommentId = -1;
			
			con = pool.getConnection();
			String sql = "insert into comment_tbl values (0, ?, 0, ?, ?, 0, now())";
			pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setInt(1, postId);
			pstmt.setString(2, userId);
			pstmt.setString(3, comment);
			flag = pstmt.executeUpdate() == 1;
			
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					generatedCommentId = generatedKeys.getInt(1);
	            } else {
	                throw new SQLException("Creating user failed, no ID obtained.");
	            }
			}
			if (generatedCommentId != -1) {
				pstmt.close();
				sql = "update comment_tbl set comment_tbl_ref_id = ? where id = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, generatedCommentId);
				pstmt.setInt(2, generatedCommentId);
			
				flag = pstmt.executeUpdate() == 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean deleteAllComments(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
				
		try {
			con = pool.getConnection();
			String sql = "delete from comment_tbl where post_tbl_id = ?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, postId);
			
			flag = pstmt.executeUpdate() >= 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean deleteComment(int commentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = hasReply(commentId) ?
					"update comment_tbl set comment = '삭제된 댓글입니다.' where id = ?" :
					"delete from comment_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, commentId);
			
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean updateComment(CommentBean comment) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			con = pool.getConnection();
			String sql = "update comment_tbl set comment = ? where id = ?";
			pstmt = con.prepareStatement(sql);
					
			pstmt.setString(1, comment.getComment());
			pstmt.setInt(2, comment.getId());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
		e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public CommentBean getComment(int commentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		CommentBean comment = null;
		
		try {
			con = pool.getConnection();
			String sql = "select * from comment_tbl where id = ?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, commentId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				comment = ResultSetExtractHelper
						.extractCommentBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return comment;
	}
	
	@Override
	public boolean hasReply(int commentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean flag = false;
		
		try {
			con = pool.getConnection();
			String sql = "select * from comment_tbl where post_tbl_id = ?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, commentId);
			
			rs = pstmt.executeQuery();
			
			flag = rs.next() && rs.getInt(1) > 1;			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return flag;
	}
	
	@Override
	public Vector<CommentBean> getComments(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<CommentBean> vlist = new Vector<CommentBean>();
		
		try {
			con = pool.getConnection();
			String sql = "select * from comment_tbl where post_tbl_id = ? order by comment_tbl_ref_id";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, postId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractCommentBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public boolean createReply(CommentBean comment) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if(comment != null) {		
			try {
				con = pool.getConnection();
				String sql = "insert into comment_tbl values (0, ?, ?, ?, ?, 1, now())";
				pstmt = con.prepareStatement(sql);
				
				pstmt.setInt(1, comment.getPostId());
				pstmt.setInt(2, comment.getRefId());
				pstmt.setString(3, comment.getUserId());
				pstmt.setString(4, comment.getComment());
				
				flag = pstmt.executeUpdate() == 1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				pool.freeConnection(con, pstmt);
			}
		}
		return flag;
	}
	
	@Override
	public int getTotalCommentCount(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;

		try {
			String sql = "select count(*) from comment_tbl "
					+ "where post_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, postId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return result;
	}
	
	@Override
	public Vector<ViewCommentBean> getCommentViews(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewCommentBean> vlist = new Vector<>();
		
		try {
			con = pool.getConnection();
			String sql = "select * from comment_view where post_id = ? order by ref_id";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, postId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewCommentBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
