package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.ResultSetExtractHelper;
import model.JobBean;
import model.UnitBean;
import model.view.ViewUnitBean;
import model.view.ViewUserBean;
import service.JobService;
import service.UnitService;

public class UnitServiceImpl implements UnitService {
	private final DBConnectionServiceImpl pool;

	public UnitServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}

	@Override
	public boolean createUnit(HttpSession session, HttpServletRequest request) {
		boolean flag = false;

		if (session != null && request != null) {
			UnitBean unit = new UnitBean();
			
			unit.setId(request.getParameter("id"));
			unit.setChargerId(request.getParameter("chargerId"));
			unit.setDocumentId(Integer.parseInt(request.getParameter("documentId")));
			unit.setName(request.getParameter("name"));
			unit.setCreateAt(request.getParameter("createAt"));
			
			flag = createUnit(session, unit);
		}
		return flag;
	}

	@Override
	public boolean createUnit(HttpSession session, UnitBean unit) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && unit != null) {
			try {
				String sql = "insert into unit_tbl values(?, ?, ?, ?, 1, ?)";
				con = pool.getConnection();
				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, unit.getId());
				pstmt.setString(2, unit.getChargerId());
				pstmt.setLong(3, unit.getDocumentId());
				pstmt.setString(4, unit.getName());
				pstmt.setString(5, unit.getCreateAt());
				
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
	public boolean deleteUnit(HttpSession session, String unitId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && unitId != null) {
			try {
				con = pool.getConnection();
				String sql = "delete from unit_tbl where id = ? ";
				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, unitId);
				
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
	public boolean updateUnit(HttpSession session, HttpServletRequest request) {
		boolean flag = false;

		if (session != null && request != null) {
			UnitBean unit = new UnitBean();

			unit.setId(request.getParameter("id"));
			unit.setChargerId(request.getParameter("chargerId"));
			unit.setDocumentId(Integer.parseInt(request.getParameter("documentId")));
			unit.setName(request.getParameter("name"));
			unit.setStatus(Integer.parseInt(request.getParameter("status")));
			unit.setCreateAt(request.getParameter("createAt"));

			flag = updateUnit(session, unit);
		}
		return flag;
	}

	@Override
	public boolean updateUnit(HttpSession session, UnitBean unit) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && unit != null) {
			try {
				String sql = "update unit_tbl set user_tbl_charger_id = ?, "
						+ "document_tbl_id = ?, name = ?, status = ? where id = ? ";
				con = pool.getConnection();
				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, unit.getChargerId()); // userID가 들어옴
				pstmt.setLong(2, unit.getDocumentId());
				pstmt.setString(3, unit.getName());
				pstmt.setInt(4, unit.getStatus());
				pstmt.setString(5, unit.getId());

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
	public int getUnitCount(String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "name", "charger", "document", "status", "date"
			};
			final String[] values = {
				"id", "unit_name", "user_name", "document_name", "status", "created_at"
			};
			String additional = "";
			boolean flag = false;
			int index = 1;

			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (keyfield != null && !keyfield.trim().equals("")) {
				if (fields.containsKey(keyfield)) {
					additional = "where " + fields.get(keyfield) + " like ? ";
					flag = true;
				}
			}
			String sql = "select count(*) from unit_view " + additional;
			pstmt = con.prepareStatement(sql);

			if (flag) {
				pstmt.setString(index++, '%' + keyword + '%');
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
	public UnitBean getUnit(HttpSession session, String unitId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		UnitBean unit = null;

		try {
			con = pool.getConnection();
			String sql = "select * from unit_tbl where id = ?";
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, unitId);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				unit = ResultSetExtractHelper
						.extractUnitBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return unit;
	}

	@Override
	public Vector<ViewUnitBean> getUnits(String keyfield, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewUnitBean> vlist = new Vector<>();

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "name", "charger", "document", "status", "date"
			};
			final String[] values = {
				"id", "unit_name", "user_name", "document_name", "status", "created_at"
			};
			String additional = "";
			boolean flag = false;
			int index = 1;

			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (keyfield != null && !keyfield.trim().equals("")) {
				if (fields.containsKey(keyfield)) {
					additional = "where " + fields.get(keyfield) + " like ? ";
					flag = true;
				}
			}
			String sql = "select * from unit_view " + additional + "order by id limit ?, ?";
			pstmt = con.prepareStatement(sql);

			if (flag) {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewUnitBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
