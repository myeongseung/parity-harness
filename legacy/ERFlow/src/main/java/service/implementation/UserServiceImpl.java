package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.ResultSetExtractHelper;
import model.UserBean;
import model.view.ViewUserBean;
import service.UserService;

public class UserServiceImpl implements UserService {
	private final DBConnectionServiceImpl pool;

	private PasswordHashServiceImpl hashSvc;
	private PermissionServiceImpl permissionSvc;

	public UserServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
		hashSvc = new PasswordHashServiceImpl();
		permissionSvc = new PermissionServiceImpl();
	}

	@Override
	public void logout(HttpSession session) {
		session.removeAttribute("user");
		session.invalidate();
	}

	@Override
	public boolean changePassword(String userId, String password) {
		UserBean user = getUser(userId);
		boolean result = false;

		if (user != null) {
			result = changePassword(user, password);
		}
		return result;
	}

	@Override
	public boolean changePassword(UserBean user, String password) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (user != null && password != null) {
			try {
				String sql = "update user_tbl set password = ? where id = ?";
				con = pool.getConnection();
				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, hashSvc.generatePassword(password));
				pstmt.setString(2, user.getId());

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
	public boolean deleteUser(HttpSession session, String userId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && userId != null) {
			if (permissionSvc.isAdmin(session)) {
				try {
					con = pool.getConnection();
					String sql = "update user_tbl set left_at = now() where id = ?";
					pstmt = con.prepareStatement(sql);

					pstmt.setString(1, userId);

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
	public boolean deleteUser(HttpSession session, UserBean user) {
		boolean result = false;

		if (session != null && user != null) {
			result = deleteUser(session, user.getId());
		}
		return result;
	}

	@Override
	public boolean register(HttpSession session, UserBean user) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && user != null) {
			if (permissionSvc.isAdmin(session)) {
				try {
					con = pool.getConnection();
					String sql = "insert into user_tbl (id, job_tbl_id, dept_tbl_id,"
							+ "name, password, social_number, postal_code, address1, address2, "
							+ "extension_phone, mobile_phone, email, hired_at) "
							+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
					pstmt = con.prepareStatement(sql);

					pstmt.setString(1, user.getId());
					pstmt.setInt(2, user.getJobId());
					pstmt.setInt(3, user.getDeptId());
					pstmt.setString(4, user.getName());
					pstmt.setString(5, hashSvc.generatePassword(user.getId()));
					pstmt.setString(6, user.getSocialNumber());
					pstmt.setString(7, user.getPostalCode());
					pstmt.setString(8, user.getAddress1());
					pstmt.setString(9, user.getAddress2());
					pstmt.setString(10, user.getExtensionPhone());
					pstmt.setString(11, user.getMobilePhone());
					pstmt.setString(12, user.getEmail());

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
	public boolean register(HttpSession session, HttpServletRequest request) {
		boolean result = false;

		if (session != null && request != null) {
			if (permissionSvc.isAdmin(session)) {
				UserBean user = new UserBean();
				user.setId(request.getParameter("id"));
				user.setJobId(Integer.parseInt(request.getParameter("job")));
				user.setDeptId(Integer.parseInt(request.getParameter("dept")));
				user.setPostalCode(request.getParameter("postal_code"));
				user.setAddress1(request.getParameter("address1"));
				user.setAddress2(request.getParameter("address2"));
				user.setName(request.getParameter("name"));
				user.setPassword(request.getParameter("id"));
				user.setSocialNumber(request.getParameter("social_number"));
				user.setExtensionPhone(request.getParameter("extension_phone"));
				user.setMobilePhone(request.getParameter("mobile_phone"));
				user.setEmail(request.getParameter("email"));
				result = register(session, user);
			}
		}
		return result;
	}

	@Override
	public boolean updateUser(HttpSession session, UserBean user) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && user != null) {
			UserBean currentUser = (UserBean) session.getAttribute("user");
			// 관리자이거나, 사용자 자기 자신인 경우 수정 가능.
			if ((currentUser != null && user.getId().equals(currentUser.getId())) || permissionSvc.isAdmin(session)) {
				try {
					con = pool.getConnection();
					String sql = "update user_tbl set name = ?, email = ?, postal_code = ?, "
							+ "address1 = ?, address2 = ?, job_tbl_id = ?, dept_tbl_id = ?, "
							+ "extension_phone = ?, mobile_phone = ? where id = ?";
					pstmt = con.prepareStatement(sql);

					pstmt.setString(1, user.getName());
					pstmt.setString(2, user.getEmail());
					pstmt.setString(3, user.getPostalCode());
					pstmt.setString(4, user.getAddress1());
					pstmt.setString(5, user.getAddress2());
					pstmt.setInt(6, user.getJobId());
					pstmt.setInt(7, user.getDeptId());
					pstmt.setString(8, user.getExtensionPhone());
					pstmt.setString(9, user.getMobilePhone());
					pstmt.setString(10, user.getId());

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
	public int getUserTotalCount(String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;

		try {
			String additional = "";
			boolean flag = keyfield != null && !keyfield.trim().equals("");

			if (flag) {
				keyfield = keyfield.trim();

				if (keyfield.equals("all")) {
					flag = false;
				} else {
					additional = keyfield + " like ? and ";
				}
			}
			String sql = "select count(*) from user_view where " + additional + "id <> 'admin'";
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
	public int getUserTotalCount(String keyfield1, String keyfield2, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;

		try {
			String additional = "";
			boolean hasKeyword = false;
			int flag = 0;

			if (keyfield1 != null && !keyfield1.trim().equals("")) {
				additional += " and dept_name like ?";
				flag = 1;
			}
			if (keyfield2 != null && !keyfield2.trim().equals("")) {				
				if (flag == 0) {
					flag = 2;
				} else {
					flag = 3;
				}
				additional += " and job_name like ?";
			}
			if (keyword != null & !keyword.trim().equals("")) {
				additional += " and name like ?";
				hasKeyword = true;
			}
			final String[] params = { keyfield1, keyfield2 };
			String sql = "select count(*) from user_view where id <> 'admin'" + additional;
			int index = 1;

			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			for (int i = 0; i < 2; ++i) {
				if ((flag & (1 << i)) != 0) {
					pstmt.setString(index++, params[i]);
				}
			}
			if (hasKeyword) {
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
	public UserBean getUser(String id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		UserBean bean = null;

		try {
			String sql = "select * from user_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean = ResultSetExtractHelper.extractUserBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	@Override
	public UserBean login(String id, String password) {
		// If the id in database, it'll be loaded to UserBean.
		UserBean user = getUser(id);

		// If the id isn't exists, the field 'user' will filled with null.
		if (user != null) {
			String userPassword = user.getPassword();

			// If the hashed password is not equals to database password,
			// it means input password is not valid with database password.
			if (!hashSvc.isValidPassword(password, userPassword)) {
				user = null;
			}
		}
		return user;
	}

	@Override
	public ViewUserBean getUserView(String id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ViewUserBean user = null;

		try {
			con = pool.getConnection();
			String sql = "select * from user_view where id = ?";
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				user = ResultSetExtractHelper.extractUserViewBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return user;
	}

	@Override
	public Vector<UserBean> getUsers() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<UserBean> users = new Vector<>();

		try {
			String sql = "select * from user_tbl where id <> 'admin'";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				users.addElement(ResultSetExtractHelper.extractUserBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return users;
	}
	
	// For User side!! Don't use this method on administrator privilege.
	@Override
	public Vector<ViewUserBean> getUserViews(String keyfield1, String keyfield2, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewUserBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean hasKeyword = false;
			int flag = 0;

			if (keyfield1 != null && !keyfield1.trim().equals("")) {
				additional += " and dept_name like ?";
				flag = 1;
			}
			if (keyfield2 != null && !keyfield2.trim().equals("")) {				
				if (flag == 0) {
					flag = 2;
				} else {
					flag = 3;
				}
				additional += " and job_name like ?";
			}
			if (keyword != null & !keyword.trim().equals("")) {
				additional += " and name like ?";
				hasKeyword = true;
			}
			final String[] params = { keyfield1, keyfield2 };
			String sql = "select dept_name, job_name, id, name from user_view where id <> 'admin'" + additional;
			int index = 1;

			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			for (int i = 0; i < 2; ++i) {
				if ((flag & (1 << i)) != 0) {
					pstmt.setString(index++, '%' + params[i] + '%');
				}
			}
			if (hasKeyword) {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ViewUserBean bean = new ViewUserBean();

				bean.setDeptName(rs.getString("dept_name"));
				bean.setJobName(rs.getString("job_name"));
				bean.setId(rs.getString("id"));
				bean.setName(rs.getString("name"));

				vlist.addElement(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ViewUserBean> getUserViews(String keyfield1, String keyfield2, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewUserBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean hasKeyword = false;
			int flag = 0;

			if (keyfield1 != null && !keyfield1.trim().equals("")) {
				additional += " and dept_name like ?";
				flag = 1;
			}
			if (keyfield2 != null && !keyfield2.trim().equals("")) {				
				if (flag == 0) {
					flag = 2;
				} else {
					flag = 3;
				}
				additional += " and job_name like ?";
			}
			if (keyword != null & !keyword.trim().equals("")) {
				additional += " and name like ?";
				hasKeyword = true;
			}
			final String[] params = { keyfield1, keyfield2 };
			String sql = "select dept_name, job_name, id, name from user_view where id <> 'admin'" + additional + " limit ?, ?";
			int index = 1;

			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			for (int i = 0; i < 2; ++i) {
				if ((flag & (1 << i)) != 0) {
					pstmt.setString(index++, '%' + params[i] + '%');
				}
			}
			if (hasKeyword) {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ViewUserBean bean = new ViewUserBean();

				bean.setDeptName(rs.getString("dept_name"));
				bean.setJobName(rs.getString("job_name"));
				bean.setId(rs.getString("id"));
				bean.setName(rs.getString("name"));

				vlist.addElement(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}

	@Override
	public Vector<ViewUserBean> getUserViews(String keyfield, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 1;
		Vector<ViewUserBean> vlist = new Vector<>();

		try {
			String additional = "";
			boolean flag = keyfield != null && !keyfield.trim().equals("");

			if (flag) {
				keyfield = keyfield.trim();

				if (keyfield.equals("all")) {
					flag = false;
				} else {
					additional = keyfield + " like ? and ";
				}
			}
			String sql = "select * from user_view where " + additional
					+ "id <> 'admin' order by dept_name, job_name limit ?, ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			if (flag) {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractUserViewBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}