package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.ResultSetExtractHelper;
import model.PostBean;
import model.view.ViewPostBean;
import service.PostService;

public class PostServiceImpl implements PostService {
	private final DBConnectionServiceImpl pool;
	
	public PostServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}
	
	@Override
	public int createPost(Map<String, String> params) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int result = -1;
		
		try {
			con = pool.getConnection();
			sql = "select max(id) from post_tbl";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			int depth = 1;
			
			if(rs.next()) {
				depth = rs.getInt(1) + 1;
			}
			sql = "insert into post_tbl (user_tbl_id, board_tbl_id, subject, content, depth, pos, count, created_at) "
					+ "values (?, ?, ?, ?, ?, 0, 0, now())";
			
			String userId = params.get("userId");
			int boardId = Integer.parseInt(params.get("boardId"));
			String content = params.get("content");
			String subject = params.get("subject");
			
			pstmt.close();
			pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, userId);
			pstmt.setInt(2, boardId);
			pstmt.setString(3, subject);
			pstmt.setString(4, content);
			pstmt.setInt(5, depth);

			int cnt = pstmt.executeUpdate();
			
			if (cnt == 1) {
			    ResultSet generatedKeys = pstmt.getGeneratedKeys();
			    			    
			    if (generatedKeys.next()) {
			    	String refSql = "update post_tbl set post_tbl_ref_id = ? where id = ?";

			        result = generatedKeys.getInt(1);
			        
				    pstmt.close();
			        
			        pstmt = con.prepareStatement(refSql);
			        
			        pstmt.setInt(1, result);
			        pstmt.setInt(2, result);
			        
			        pstmt.executeUpdate();
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return result;
	}

	@Override
	public boolean deletePost(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		ResultSet rs = null;
		
		try {
			con = pool.getConnection();
			// 게시물에 답글이 있는지 확인
	        String sql = "select count(*) from post_tbl where post_tbl_ref_id = ?";
	        pstmt = con.prepareStatement(sql);
	        pstmt.setInt(1, postId);
	        rs = pstmt.executeQuery();
	        
	        if(rs.next()) { 
	        	int replyCount = rs.getInt(1);
	        	
	        	if(replyCount <= 1) { // 답변글이 없으면
	        		String deleteSql = "delete from post_tbl where id = ?";
	        		pstmt.close();
	        		pstmt = con.prepareStatement(deleteSql)	;
	        		pstmt.setInt(1, postId);
	        		pstmt.executeUpdate();
	        		flag = true;
	        	} else {
	        		// 답변글이 1개 이상이면 게시물 제목을 "삭제된 글입니다."
	        		String updateSql = "update post_tbl set subject = '삭제된 글입니다.', content ='삭제된 글입니다.', `delete`=1 WHERE id = ?";
	        		pstmt.close();
	        		pstmt = con.prepareStatement(updateSql);
	        		pstmt.setInt(1, postId);
	        		pstmt.executeUpdate();
	        		flag = true;
	        	}
	        }    
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean replyPost(PostBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			int boardId = 2; // 외래키오류때문에 임시
			String sql = "insert into post_tbl (user_tbl_id, board_tbl_id, post_tbl_ref_id, subject, content, depth, pos, count, `delete`, created_at) "
			           + "values (?, ?, ?, ?, ?, ?, ?, 0, 0, now())";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bean.getUserId());
	        pstmt.setInt(2, boardId);
	        pstmt.setInt(3, bean.getRefId());
	        pstmt.setString(4, bean.getSubject());
	        pstmt.setString(5, bean.getContent());
	        pstmt.setInt(6, bean.getDepth());
			pstmt.setInt(7, bean.getPos() + 1);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean replyPosUp(int refId, int pos) {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    boolean flag = false;
	    
	    try {
	        con = pool.getConnection();
	        String sql = "update post_tbl set pos = pos + 1 where post_tbl_ref_id = ? and pos > ?";
	        pstmt = con.prepareStatement(sql);
	        pstmt.setInt(1, refId);
	        pstmt.setInt(2, pos);

	        flag = pstmt.executeUpdate() == 1;
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        pool.freeConnection(con, pstmt);
	    }
	    return flag;
	}
	
	@Override
	public boolean updatePost(Map<String, String> params) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
	        String sql = "update post_tbl set subject = ?, content = ? where id = ?";
	        con = pool.getConnection();
	        
	        int id = Integer.parseInt(params.get("id"));
	        String subject = params.get("subject");
	        String content = params.get("content");
	        
	        pstmt = con.prepareStatement(sql);
	        
	        pstmt.setString(1, subject);
	        pstmt.setString(2, content);
	        pstmt.setInt(3, id);
	        
	        flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean updateCount(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "update post_tbl set count = count+1 where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, postId);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public int getRecentId() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "select max(id) from post_tbl";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return -1;
	}

	@Override
	public int getTotalCount(int boardId, String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = -1;
		
		try {
			String additional = "";
			boolean flag = false;
			
			if (keyword != null && !keyword.trim().equals("")) {
				switch (keyfield) {
					case "subject" -> {
						additional = " subject like ? and";
						flag = true;
					}
					case "author" -> {
						additional = " name like ? and";
						flag = true;
					}
				}
			}
			String sql = "select count(*) from post_view where"
					+ additional + " board_id = ?";
			int index = 1;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			pstmt.setInt(index++, boardId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return count;
	}

	@Override
	public PostBean getPost(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PostBean post = null;

		try {
			String sql = "select * from post_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, postId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				post = ResultSetExtractHelper
						.extractPostBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return post;
	}
	
	@Override
	public ViewPostBean getPostView(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ViewPostBean post = null;

		try {
			String sql = "select * from post_view where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, postId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				post = ResultSetExtractHelper
						.extractViewPostBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return post;
	}
	
	@Override
	public Vector<PostBean> getPosts(int boardId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<PostBean> vlist = new Vector<>();

		try {
			String sql = "select * from post_tbl where board_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, boardId);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractPostBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}

	@Override
	public Vector<PostBean> getPosts(int boardId, String keyfield, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<PostBean> vlist = new Vector<>();

		try {
			String sql = "select * from post_tbl where board_tbl_id = ? order by post_tbl_ref_id desc, pos limit cnt";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, boardId);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractPostBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ViewPostBean> getPostViews(int boardId, String keyfield, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewPostBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean flag = false;
			
			if (keyfield != null && !keyfield.trim().equals("")) {
				switch (keyfield) {
					case "subject" -> {
						additional = " subject like ? and";
						flag = true;
					}
					case "author" -> {
						additional = " name like ? and";
						flag = true;
					}
				}
			}
			String sql = "select * from post_view where" +
					additional + " board_id = ? order by depth desc, pos limit ?, ?"; 
			int index = 1;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			// Keyfield가 있을 경우
			if (flag) {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			pstmt.setInt(index++, boardId);
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewPostBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
