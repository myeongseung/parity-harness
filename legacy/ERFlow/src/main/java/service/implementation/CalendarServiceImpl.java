package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import controller.PermissionController;
import helper.ResultSetExtractHelper;
import helper.WebHelper;
import model.CalendarBean;
import model.UserBean;
import model.view.ViewCalendarBean;
import service.CalendarService;

public class CalendarServiceImpl implements CalendarService {
	private final DBConnectionServiceImpl pool;

	public CalendarServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}

	@Override
	public Vector<ViewCalendarBean> getCalendarViews(HttpSession session) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewCalendarBean> vlist = new Vector<>();

		if (session != null) {
			if (WebHelper.isLogin(session)) {
				UserBean user = WebHelper.getValidUser(session);

				try {
					String sql = "select * from calendar_view where type = 2 or "
							+ "(type = 1 and dept_id = ?) or (type = 0 and user_id = ?)";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);

					pstmt.setInt(1, user.getDeptId());
					pstmt.setString(2, user.getId());

					rs = pstmt.executeQuery();

					while (rs.next()) {
						vlist.addElement(ResultSetExtractHelper.extractCalendarViewBean(rs));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pool.freeConnection(con, pstmt, rs);
				}
			}
		}
		return vlist;
	}

	@Override
	public boolean createCalendar(CalendarBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "insert into calendar_tbl (user_tbl_id, subject, content, started_at, ended_at, type)"
					+ " values (?, ?, ?, ?, ?, ?)";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, bean.getUserId());
			pstmt.setString(2, bean.getSubject());
			pstmt.setString(3, bean.getContent());
			pstmt.setString(4, bean.getStartedAt());
			pstmt.setString(5, bean.getEndedAt());
			pstmt.setInt(6, bean.getType());

			flag = pstmt.executeUpdate() == 1;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean updateCalendar(CalendarBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (bean != null) {
			try {
				String sql = "update calendar_tbl set subject = ?, content = ?, started_at = ?, "
						+ "ended_at = ?, type = ? where id = ?";
				con = pool.getConnection();
				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, bean.getSubject());
				pstmt.setString(2, bean.getContent());
				pstmt.setString(3, bean.getStartedAt());
				pstmt.setString(4, bean.getEndedAt());
				pstmt.setInt(5, bean.getType());
				pstmt.setInt(6, bean.getId());

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
	public boolean deleteCalendar(CalendarBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "delete from calendar_tbl where id = ? and user_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, bean.getId());
			pstmt.setString(2, bean.getUserId());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
}
