package service.implementation;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import helper.ResultSetExtractHelper;
import helper.WebHelper;
import model.CompanyBean;
import model.ProcessBean;
import model.view.ViewTaskBean;
import service.ProcessService;

public class ProcessServiceImpl implements ProcessService {
	private final DBConnectionServiceImpl pool;

	private PermissionServiceImpl permissionSvc;

	public ProcessServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}

	/*
	 * 
	 * */

	@Override
	public boolean createProcess(HttpServletRequest request) {
		ProcessBean bean = new ProcessBean();
		if (request != null) {
			bean.setId(request.getParameter("id"));
			bean.setPrevId(request.getParameter("prevId"));
			bean.setNextId(request.getParameter("nextId"));
			bean.setName(request.getParameter("name"));
			bean.setPriority(WebHelper.parseInt(request, "priority"));
		}
		return createProcess(bean);
	}

	@Override
	public boolean createProcess(ProcessBean process) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = null;
			con = pool.getConnection();
			sql = "insert into process_tbl values(?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, process.getId());
			pstmt.setString(2, process.getPrevId());
			pstmt.setString(3, process.getNextId());
			pstmt.setString(4, process.getName());
			pstmt.setInt(5, process.getPriority());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean deleteProcess(String processId) {
		Connection con = null;
		CallableStatement cstmt = null;
		boolean flag = false;

		try {
			String sql = "{call DeleteProcess(?)}";
			con = pool.getConnection();
			cstmt = con.prepareCall(sql);
			
			cstmt.setString(1, processId);
			flag = cstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, cstmt);
		}
		return flag;
	}

	@Override
	public boolean updateProcess(HttpServletRequest request) {
		ProcessBean bean = new ProcessBean();
		if (request != null) {
			bean.setId(request.getParameter("id"));
			bean.setName(request.getParameter("name"));
		}
		return updateProcess(bean);
	}

	@Override
	public boolean updateProcess(ProcessBean process) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "update process_tbl set name = ? where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, process.getName());
			pstmt.setString(2, process.getId());
			
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public ProcessBean getProcess(String processId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ProcessBean bean = null;
		try {
			String sql = "select * from process_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, processId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				bean = ResultSetExtractHelper.extractProcessBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	@Override
	public Vector<ProcessBean> getProcesses(String keyfield, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ProcessBean> processes = new Vector<>();

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "name"
				};
			final String[] values = {
				"id", "name"
			};
			String additional = "";
			int flag = 0;
			int index = 1;
			
			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (fields.containsKey(keyfield)) {
				additional = " where " + fields.get(keyfield) + " like ?";
				flag = 1;
			} else if (keyfield.equals("priority")) {
				additional = " where priority = ?";
				flag = 2;
			}
			String sql = "select * from process_tbl" + additional + 
					" order by id limit ?, ?";
			pstmt = con.prepareStatement(sql);
			
			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setInt(index++, Integer.parseInt(keyword));
				}
			}
			
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				processes.addElement(ResultSetExtractHelper.extractProcessBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return processes;
	}

	@Override
	public int processesCount(String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "name"
				};
			final String[] values = {
				"id", "name"
			};
			String additional = "";
			int flag = 0;
			int index = 1;
			
			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (fields.containsKey(keyfield)) {
				additional = " where " + fields.get(keyfield) + " like ?";
				flag = 1;
			} else if (keyfield.equals("priority")) {
				additional = " where priority = ?";
				flag = 2;
			}
			String sql = "select count(*) from process_tbl" + additional;
			pstmt = con.prepareStatement(sql);
			
			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setInt(index++, Integer.parseInt(keyword));
				}
			}

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
}
