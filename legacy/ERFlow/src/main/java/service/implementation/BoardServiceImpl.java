package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.ResultSetExtractHelper;
import helper.WebHelper;
import model.BoardBean;
import service.BoardService;

public class BoardServiceImpl implements BoardService {
	private final DBConnectionServiceImpl pool;
	
	private PermissionServiceImpl permissionSvc;
	
	public BoardServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
		
		permissionSvc = new PermissionServiceImpl();
	}
	
	@Override
	public boolean createBoard(HttpSession session, BoardBean board) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if (session != null && board != null) {
			if (permissionSvc.isAdmin(session)) {
				try {
					String sql = "insert into board_tbl (subject, created_at, "
							+ "permission_read_dept_level, permission_read_job_level,"
							+ "permission_write_dept_level, permission_write_job_level) "
							+ "values (?, now(), ?, ?, ?, ?)";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setString(1, board.getSubject());
					pstmt.setLong(2, Long.MIN_VALUE);
					pstmt.setLong(3, Long.MIN_VALUE);
					pstmt.setLong(4, Long.MIN_VALUE);
					pstmt.setLong(5, Long.MIN_VALUE);

					flag = pstmt.executeUpdate() == 1;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pool.freeConnection(con, pstmt);
				}
			}
		}
		return flag;
	}

	@Override
	public boolean createBoard(HttpSession session, HttpServletRequest request) {
		boolean result = false;
		
		if (session != null && request != null) {
			BoardBean board = new BoardBean();
			
			board.setSubject(request.getParameter("subject"));
			
			result = createBoard(session, board);
		}
		return result;
	}

	@Override
	public boolean deleteBoard(HttpSession session, int boardId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "delete from board_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, boardId);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean hasBoardName(String name) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean flag = false;

		try {
			String sql = "select count(subject) from board_tbl where subject = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, name);
			
			rs = pstmt.executeQuery();

			flag = rs.next() && rs.getInt(1) == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return flag;
	}

	@Override
	public boolean updateBoard(HttpSession session, BoardBean board) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && board != null) {
			if (permissionSvc.isAdmin(session)) {
				try {
					String sql = "update board_tbl set subject = ? where id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setString(1, board.getSubject());
					pstmt.setInt(2, board.getId());

					flag = pstmt.executeUpdate() == 1;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pool.freeConnection(con, pstmt);
				}				
			}
		}
		return flag;
	}

	@Override
	public boolean updateBoard(HttpSession session, HttpServletRequest request) {
		boolean result = false;
		
		if (request != null) {
			BoardBean board = new BoardBean();
			
			board.setId(WebHelper.parseInt(request, "id"));
			board.setSubject(request.getParameter("subject"));
			
			result = updateBoard(session, board);
		}
		return result;
	}
	
	@Override
	public int getBoardCount(String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;

		try {
			String additional = "";
			boolean flag = false;
			
			if (keyword != null && !keyword.trim().equals("")) {
				additional = " where subject like ?";
				flag = true;
			}
			String sql = "select count(*) from board_tbl" + additional;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(1, '%' + keyword + '%');
			}
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
	public BoardBean getBoard(int boardId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardBean board = null;

		try {
			String sql = "select * from board_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, boardId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				board = ResultSetExtractHelper
						.extractBoardBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return board;
	}
	
	@Override
	public Vector<BoardBean> getBoards() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<BoardBean> vlist = new Vector<>();

		try {
			String sql = "select * from board_tbl order by subject";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractBoardBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<BoardBean> getBoards(String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<BoardBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean flag = false;
			
			if (keyword != null && !keyword.trim().equals("")) {
				additional = " where subject like ?";
				flag = true;
			}
			String sql = "select * from board_tbl" + additional;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(1, '%' + keyword + '%');
			}
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractBoardBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<BoardBean> getBoards(String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<BoardBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean flag = false;
			int index = 1;
			
			if (keyword != null && !keyword.trim().equals("")) {
				additional = " where subject like ?";
				flag = true;
			}
			String sql = "select * from board_tbl" + additional + " limit ?, ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractBoardBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
