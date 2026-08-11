package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import helper.ResultSetExtractHelper;
import model.ProgramBean;
import service.ProgramService;

public class ProgramServiceImpl implements ProgramService {
	private final DBConnectionServiceImpl pool;
	
	public ProgramServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}
	
	@Override
	public int getProgramCount(String name) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;

		try {
			String additional = "";
			boolean flag = false;
			
			if (name != null && !name.trim().equals("")) {
				additional = " where program_name = ?";
				flag = true;
			}
			String sql = "select count(*) from permission_program_tbl" + additional;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(1, name);
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
	public ProgramBean getProgram(int id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ProgramBean bean = null;
		
		try {
			String sql = "select * from permission_program_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, id);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean = ResultSetExtractHelper
						.extractProgramBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}
	
	@Override
	public ProgramBean getProgram(String programName) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ProgramBean bean = null;
		
		try {
			String sql = "select * from permission_program_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, programName);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean = ResultSetExtractHelper
						.extractProgramBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}
	
	@Override
	public Vector<ProgramBean> getPrograms(String name, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ProgramBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean flag = false;
			int index = 1;
			
			if (name != null && !name.trim().equals("")) {
				additional = " where program_name like ?";
				flag = true;
			}
			String sql = "select * from permission_program_tbl"
					+ additional + " limit ?, ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(index++, '%' + name + '%');
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);
			
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
