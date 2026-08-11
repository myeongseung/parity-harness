package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import helper.ResultSetExtractHelper;
import model.view.ViewWorkBean;
import service.WorkService;

public class WorkServiceImpl implements WorkService {
	private final DBConnectionServiceImpl pool;
	
	public WorkServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}
	
	@Override
	public Vector<ViewWorkBean> getWorkViews(String date) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewWorkBean> vlist = new Vector<>();

		try {
			String sql = "select * from work_view where started_at like ? or ended_at like ? order by user_id";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, '%' + date + '%');
			pstmt.setString(2, '%' + date + '%');
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractViewWorkBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ViewWorkBean> getWorkViews(String id, String date) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewWorkBean> vlist = new Vector<>();

		try {
			String sql = "select * from work_view where (started_at like ? or ended_at like ?) and user_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, '%' + date + '%');
			pstmt.setString(2, '%' + date + '%');
			pstmt.setString(3, id);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewWorkBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
