package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import helper.ResultSetExtractHelper;
import model.BoardBean;
import model.DepartmentBean;
import model.JobBean;
import model.ProgramBean;
import model.UserBean;
import model.view.ViewPermissionBean;
import service.PermissionService;

public class PermissionServiceImpl implements PermissionService {
	private final DBConnectionServiceImpl pool;
		
	public PermissionServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}
	
	@Override
	public boolean changeBoardReadPermission(HttpSession session, BoardBean board, long deptPermission, long jobPermission) {
		boolean result = false;
		
		if (session != null && board != null) {
			int boardId = board.getId();
			
			result = changeBoardReadPermission(session, boardId, deptPermission, jobPermission);
		}
		return result;
	}
	
	@Override
	public boolean changeBoardReadPermission(HttpSession session, int boardId, long deptPermission, long jobPermission) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && boardId > 1) {
			if (isAdmin(session)) {
				try {
					String sql = "update board_tbl set permission_read_dept_level = ?, "
							+ "permission_read_job_level = ? where id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setLong(1, deptPermission);
					pstmt.setLong(2, jobPermission);
					pstmt.setInt(3, boardId);

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
	public boolean changeBoardWritePermission(HttpSession session, BoardBean board, long deptPermission, long jobPermission) {
		boolean result = false;
		
		if (session != null && board != null) {
			int boardId = board.getId();
			
			result = changeBoardWritePermission(session, boardId, deptPermission, jobPermission);
		}
		return result;
	}
	
	@Override
	public boolean changeBoardWritePermission(HttpSession session, int boardId, long deptPermission, long jobPermission) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && boardId > 1) {
			if (isAdmin(session)) {
				try {
					String sql = "update board_tbl set permission_write_dept_level = ?, "
							+ "permission_write_job_level = ? where id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setLong(1, deptPermission);
					pstmt.setLong(2, jobPermission);
					pstmt.setInt(3, boardId);

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
	public boolean changeDeptPermission(HttpSession session, int deptId, long permission) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && deptId != 0) {
			if (isAdmin(session)) {
				try {
					String sql = "update permission_dept_tbl set permission = ? "
							+ "where dept_tbl_id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);

					pstmt.setLong(1, permission);
					pstmt.setInt(2, deptId);
					
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
	public boolean changeDeptPermission(HttpSession session, DepartmentBean dept, long permission) {
		boolean result = false;
		
		if (session != null && dept != null) {
			result = changeDeptPermission(session, dept.getId(), permission);
		}
		return result;
	}

	@Override
	public boolean changeJobPermission(HttpSession session, int jobId, long permission) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && jobId != 0) {
			if (isAdmin(session)) {
				try {
					String sql = "update permission_job_tbl set permission = ? "
							+ "where job_tbl_id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);

					pstmt.setLong(1, permission);
					pstmt.setInt(2, jobId);
					
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
	public boolean changeJobPermission(HttpSession session, JobBean job, long permission) {
		boolean result = false;
		
		if (session != null && job != null) {
			result = changeJobPermission(session, job.getId(), permission);
		}
		return result;
	}

	@Override
	public boolean changeProgramDeptPermission(HttpSession session, String programId, long permission) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if (session != null && programId != null) {
			if (isAdmin(session)) {
				try {
					String sql = "update permission_program_tbl set "
							+ "dept_level = ? where program_id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setLong(1, permission);
					pstmt.setString(2, programId);

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
	public boolean changeProgramJobPermission(HttpSession session, String programId, long permission) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if (session != null && programId != null) {
			if (isAdmin(session)) {
				try {
					String sql = "update permission_program_tbl set "
							+ "job_level = ? where program_id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setLong(1, permission);
					pstmt.setString(2, programId);

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
	public boolean deleteDeptPermission(HttpSession session, int deptId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "delete from permission_dept_tbl where dept_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, deptId);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean deleteJobPermission(HttpSession session, int jobId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "delete from permission_job_tbl where job_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, jobId);
			
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean isAdmin(HttpSession session) {
		boolean result = false;
		
		if (session != null) {
			UserBean user = (UserBean)session.getAttribute("user");
			
			if (user != null) { 
				result = isAdmin(user);
			}
		}
		return result;
	}

	@Override
	public boolean isAdmin(UserBean user) {
		boolean result = false;
		
		if (user != null) {
			long deptPermission = getDeptPermission(user); 
			long jobPermission = getJobPermission(user);
			
			result = isAdmin(deptPermission, jobPermission);
		}
		return result;
	}

	@Override
	public boolean isAdmin(long deptPermission, long jobPermission) {
		return (deptPermission & Long.MIN_VALUE) == Long.MIN_VALUE
				&& (jobPermission & Long.MIN_VALUE) == Long.MIN_VALUE;
	}
	
	@Override
	public boolean revokeDeptPermission(HttpSession session, int deptId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		long permission = getDeptPermission(deptId);

		try {
			String sql = "update permission_dept_tbl set permission = permission & ~? "
					+ "where dept_tbl_id <> ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setLong(1, permission);
			pstmt.setInt(2, deptId);
			
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean revokeJobPermission(HttpSession session, int jobId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		long permission = getJobPermission(jobId);

		try {
			String sql = "update permission_job_tbl set permission = permission & ~? "
					+ "where job_tbl_id <> ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setLong(1, permission);
			pstmt.setInt(2, jobId);
			
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public long getBoardPermission(String category, String flag, int boardId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long result = -1L;

		try {
			con = pool.getConnection();
			String sql = "select permission_" + flag + "_" + category + "_level " +
					"from board_tbl where id = ?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, boardId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return result;
	}
	
	@Override
	public long getBoardReadDeptPermission(int boardId) {
		return getBoardPermission("dept", "read", boardId);
	}
	
	@Override
	public long getBoardReadJobPermission(int boardId) {
		return getBoardPermission("job", "read", boardId);
	}
	
	@Override
	public long getBoardWriteDeptPermission(int boardId) {
		return getBoardPermission("dept", "write", boardId);
	}
	
	@Override
	public long getBoardWriteJobPermission(int boardId) {
		return getBoardPermission("job", "write", boardId);
	}
	
	@Override
	public long getDeptPermission(UserBean user) {
		long result = 0L;
		
		if (user != null) {
			result = getDeptPermission(user.getDeptId());
		}
		return result;
	}

	@Override
	public long getDeptPermission(int deptId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long result = 0L;

		try {
			String sql = "select permission from permission_dept_tbl "
					+ "where dept_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, deptId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getLong("permission");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return result;
	}

	@Override
	public long getJobPermission(UserBean user) {
		long result = 0L;
		
		if (user != null) {
			result = getJobPermission(user.getJobId());
		}
		return result;
	}

	@Override
	public long getJobPermission(int jobId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long result = 0L;

		try {
			String sql = "select permission from permission_job_tbl "
					+ "where job_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, jobId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getLong("permission");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return result;
	}
	
	@Override
	public long getProgramDeptPermission(String programId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long result = 0L;

		try {
			String sql = "select dept_level from permission_program_tbl "
					+ "where program_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, programId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return result;
	}
	
	@Override
	public long getProgramJobPermission(String programId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long result = 0L;

		try {
			String sql = "select job_level from permission_program_tbl "
					+ "where program_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, programId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return result;
	}
	
	@Override
	public long next(String flag) {
		long result = 0L;
		long permission = getTotalPermission(flag);
		
		for (int i = 0; i < 64 && permission != 0L; ++i) {
			// If find the blank position, it can allocate new position.
			if ((permission & 1) == 0) {
				result = 1 << i;
				// Literally equals to break sequence.
				i = 64;		 
			}
			permission >>= 1;
		}
		return result;
	}
	
	private long getTotalPermission(String flag)
			throws IllegalArgumentException {
		long result = 0L;
		
		switch (flag) {
			case "dept" -> {
				result = getDeptTotalPermission();
			}
			case "job" -> {
				result = getJobTotalPermission();
			}
			default -> {
				throw new IllegalArgumentException();
			}
		}
		return result;
	}
	
	private long getDeptTotalPermission() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<Long> vlist = new Vector<>();
		long result = 0;

		try {
			String sql = "select level from permission_dept_tbl";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(rs.getLong(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		for (long t : vlist) {
			result |= t;
		}
		return result;
	}
	
	private long getJobTotalPermission() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<Long> vlist = new Vector<>();
		long result = 0;

		try {
			String sql = "select level from permission_job_tbl";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(rs.getLong(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		for (long t : vlist) {
			result |= t;
		}
		return result;
	}
	
	@Override
	public Vector<ViewPermissionBean> getDeptPermissions(String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewPermissionBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean flag = false;
			
			if (keyword != null && !keyword.trim().equals("")) {
				additional = "name like ? and ";
				flag = true;
			}
			String sql = "select * from permission_dept_view where "
					+ additional + "dept_id <> -1";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(1, '%' + keyword + '%');
			}
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewDeptPermissionBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ViewPermissionBean> getJobPermissions(String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewPermissionBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean flag = false;
			
			if (keyword != null && !keyword.trim().equals("")) {
				additional = "name like ? and ";
				flag = true;
			}
			String sql = "select * from permission_job_view where "
					+ additional + "job_id <> -1";;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(1, '%' + keyword + '%');
			}
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewJobPermissionBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ProgramBean> getProgramPermissions(String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ProgramBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean flag = false;
			
			if (keyword != null && !keyword.trim().equals("")) {
				additional = " where program_name like ?";
				flag = true;
			}
			String sql = "select * from permission_program_tbl" + additional;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(1, keyword);
			}
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractProgramBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
