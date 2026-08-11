package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import model.DepartmentBean;
import service.CodeService;

public class CodeServiceImpl implements CodeService {
	private final DBConnectionServiceImpl pool;
	
	public CodeServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	
	}
	
	@Override
	public boolean createCode(String userId, int code) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		try {
			con = pool.getConnection();
			String sql = "insert into code_tbl values (0, ?, ?, now())";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, code);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean deleteCode(String userId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		try {
			con = pool.getConnection();
			String sql = "delete from code_tbl where user_tbl_id = ?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, userId);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean isValidCode(String userId, int code) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean flag = false;
		
		try {
			con = pool.getConnection();
			String sql = "select count(*) from code_tbl where user_tbl_id = ? and code = ?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, code);
			
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
	public int generateCode() {
		Random random = new Random();
		int result = 0;
		
		for (int i = 0; i < 6; ++i) {
			result = result * 10 + random.nextInt(10);
		}
		return result;
	};
}
