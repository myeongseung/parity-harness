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
import helper.WebHelper;
import model.TaskBean;
import model.TaskHistoryBean;
import model.view.ViewBoundBean;
import model.view.ViewTaskBean;
import model.view.ViewUnitBean;
import service.TaskService;

public class TaskServiceImpl implements TaskService {
	private final DBConnectionServiceImpl pool;

	public TaskServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}

	@Override
	public boolean createTask(HttpServletRequest request, Vector<TaskHistoryBean> products) {
		TaskBean bean = new TaskBean();
		bean.setUserId(request.getParameter("userId"));
		bean.setCompanyId(WebHelper.parseInt(request, "companyId"));
		bean.setDocumentId(WebHelper.parseInt(request, "documentId"));
		bean.setType(WebHelper.parseInt(request, "type"));
		bean.setTaskAt(request.getParameter("taskAt"));
		bean.setStatus(WebHelper.parseInt(request, "status"));
		return createTask(bean, products);
	}

	@Override
	public boolean createTask(TaskBean task, Vector<TaskHistoryBean> products) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		int index = 1;

		try {
			String sql = "insert into task_tbl values (null, ?, ?, ?, ?, ?, now(), ?)";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			pstmt.setString(index++, task.getUserId());
			pstmt.setInt(index++, task.getCompanyId());
			pstmt.setInt(index++, task.getDocumentId());
			pstmt.setInt(index++, task.getType());
			pstmt.setString(index++, task.getTaskAt());
			pstmt.setInt(index++, task.getStatus());

			flag = pstmt.executeUpdate() == 1;
			int taskId = 0;
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					taskId = generatedKeys.getInt(1);
				} else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}
			pstmt.close();

			if (flag && products != null) {
				for (TaskHistoryBean bean : products) {
					bean.setTaskId(taskId);
					if (bean.getCount() > 0) {
						createTaskHistory(bean);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean deleteTask(int taskId, int type) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		int index = 1;

		try {
			deleteTaskHistory(taskId);

			String sql = "delete from task_tbl where id = ? and type = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(index++, taskId);
			pstmt.setInt(index++, type);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean updateTask(HttpServletRequest request) {
		TaskBean bean = new TaskBean();
		bean.setId(WebHelper.parseInt(request, "taskId"));
		bean.setUserId(request.getParameter("userId"));
		bean.setCompanyId(WebHelper.parseInt(request, "companyId"));
		bean.setDocumentId(WebHelper.parseInt(request, "documentId"));
		bean.setType(WebHelper.parseInt(request, "type"));
		bean.setTaskAt(request.getParameter("taskAt"));
		bean.setStatus(WebHelper.parseInt(request, "status"));

		return updateTask(bean);
	}

	@Override
	public boolean updateTask(TaskBean task) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		int index = 1;

		try {
			String sql = "update task_tbl set user_tbl_id = ?,  document_tbl_id = ?, task_at = ?, status = ? "
					+ "where id = ? and type = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(index++, task.getUserId());
			pstmt.setInt(index++, task.getDocumentId());
			pstmt.setString(index++, task.getTaskAt());
			pstmt.setInt(index++, task.getStatus());
			pstmt.setInt(index++, task.getId());
			pstmt.setInt(index++, task.getType());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public TaskBean getTask(int taskId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TaskBean bean = null;
		try {
			String sql = "select * from task_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, taskId);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				bean = ResultSetExtractHelper.extractTaskBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	@Override
	public Vector<ViewTaskBean> getTasks(String keyfield, String keyword, int start, int cnt, int type) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewTaskBean> tasks = new Vector<>();

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = { "userName", "deptName", "companyName" };
			final String[] values = { "user_name", "dept_name", "company_name" };
			String additional = "";
			String conditional = "type = ?";
			int flag = 0;
			int index = 1;

			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (type == -1) {
				conditional = "";
			}
			if (fields.containsKey(keyfield)) {
				additional = " and " + fields.get(keyfield) + " like ?";
				flag = 1;
			} else if (keyfield.equals("date")) {
				additional = " and task_at >= ?";
				flag = 2;
			} else if (keyfield.equals("status")) {
				additional = " and status = ?";
				flag = 3;
			}
			String sql = "select * from task_view"
					+ (!conditional.equals("") || !additional.equals("") ? " where " : "") + conditional + additional
					+ " order by task_id limit ?, ?";
			pstmt = con.prepareStatement(sql);

			if (type != -1) {
				pstmt.setInt(index++, type);
			}
			switch (flag) {
			case 1 -> {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			case 2 -> {
				pstmt.setString(index++, keyword);
			}
			case 3 -> {
				pstmt.setInt(index++, Integer.parseInt(keyword));
			}
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ViewTaskBean task = ResultSetExtractHelper.extractViewtTaskBean(rs);
				tasks.addElement(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return tasks;
	}

	@Override
	public int getTaskCount(String keyfield, String keyword, int type) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = { "userName", "deptName", "companyName" };
			final String[] values = { "user_name", "dept_name", "company_name" };
			String additional = "";
			int flag = 0;
			int index = 2;

			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (fields.containsKey(keyfield)) {
				additional = " and " + fields.get(keyfield) + " like ?";
				flag = 1;
			} else if (keyfield.equals("date")) {
				additional = " and task_at >= ?";
				flag = 2;
			} else if (keyfield.equals("status")) {
				additional = " and status = ?";
				flag = 3;
			}
			String sql = "select count(*) from task_view where type = ?" + additional + " order by task_id";
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, type);

			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setString(index++, keyword);
				}
				case 3 -> {
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

	@Override
	public boolean createTaskHistory(TaskHistoryBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "insert into task_history_tbl values (?, ?, ?)";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, bean.getTaskId());
			pstmt.setString(2, bean.getProductId());
			pstmt.setInt(3, bean.getCount());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean updateTaskHistory(TaskHistoryBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "update task_history_tbl set count = ? where task_tbl_id = ? and product_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, bean.getCount());
			pstmt.setInt(2, bean.getTaskId());
			pstmt.setString(3, bean.getProductId());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean deleteTaskHistory(int taskId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "delete from task_history_tbl where task_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, taskId);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public Vector<TaskHistoryBean> getTaskHistories(String keyfield, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<TaskHistoryBean> vlist = new Vector<>();

		try {
			boolean isKeyfield = false;
			int index = 1;
			String additional = "";

			if (!keyfield.trim().equals("") || keyfield != null) {
				additional = " where " + keyfield + " = ?";
				isKeyfield = true;
			}

			String sql = "select * from task_history_tbl " + additional
					+ "order by task_tbl_id limit ?, ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			if (isKeyfield) {
				pstmt.setString(index++, keyword);
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractTaskHistoryBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return vlist;
	}

	@Override
	public int getTaskHistoriesCount(String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			boolean isKeyfield = false;
			int index = 1;
			String additional = "";

			if (!keyfield.trim().equals("") || keyfield != null) {
				additional = " where " + keyfield + " = ?";
				isKeyfield = true;
			}

			String sql = "select count(*) from task_history_tbl " + additional;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			if (isKeyfield & keyfield.equals("task_tbl_id")) {
				pstmt.setInt(index++, Integer.parseInt(keyword));
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return count;
	}
}
