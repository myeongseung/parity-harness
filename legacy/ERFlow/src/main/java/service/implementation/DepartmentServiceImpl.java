package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.ResultSetExtractHelper;
import model.DepartmentBean;
import model.JobBean;
import service.DepartmentService;


public class DepartmentServiceImpl implements DepartmentService {
	private final DBConnectionServiceImpl pool;
	
	private PermissionServiceImpl permissionSvc;
	
	public DepartmentServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
		permissionSvc = new PermissionServiceImpl();
	}
	
	@Override
	public boolean createDept(HttpSession session, DepartmentBean dept) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if (session != null && dept != null) {
			if (permissionSvc.isAdmin(session)) {
				try {
					String sql = "insert into dept_tbl (name, postal_code, address1, address2) values (?, ?, ?, ?)";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					
					pstmt.setString(1, dept.getName());
					pstmt.setString(2, dept.getPostalCode());
					pstmt.setString(3, dept.getAddress1());
					pstmt.setString(4, dept.getAddress2());

					flag = pstmt.executeUpdate() == 1;
					
					try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							dept.setId(generatedKeys.getInt(1));
			            } else {
			                throw new SQLException("Creating user failed, no ID obtained.");
			            }
					}
					pstmt.close();

					long permission = permissionSvc.next("dept");
					sql = "insert into permission_dept_tbl values (0, ?, ?, ?)";
					pstmt = con.prepareStatement(sql);
					
					pstmt.setInt(1, dept.getId());
					pstmt.setLong(2, permission);
					pstmt.setLong(3, permission);
					
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
	public boolean deleteDept(HttpSession session, int deptId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if (session != null) {
			if (permissionSvc.isAdmin(session)) {
				permissionSvc.deleteDeptPermission(session, deptId);
				
				try {
					String sql = "delete from dept_tbl where id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setInt(1, deptId);
					
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
	public boolean updateDept(HttpSession session, DepartmentBean dept) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if (session != null && dept != null) {
			if (permissionSvc.isAdmin(session)) {
				try {
					con = pool.getConnection();
					String sql = "update dept_tbl set user_tbl_manager_id =?, postal_code = ?, "
							+ "address1 = ?, address2 = ?, name = ? where id = ?";
					pstmt = con.prepareStatement(sql);
					
					pstmt.setString(1, dept.getManagerId());
					pstmt.setString(2, dept.getPostalCode());
					pstmt.setString(3, dept.getAddress1());
					pstmt.setString(4, dept.getAddress2());
					pstmt.setString(5, dept.getName());
					pstmt.setInt(6, dept.getId());
					
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
	public boolean hasDept(String name) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean flag = false;

		try {
			String sql = "select count(*) from dept_tbl where name = ?";
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
	public DepartmentBean getDept(int deptId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DepartmentBean dept = null;
		
		try {
			String sql = "select * from dept_tbl where id = ? ";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, deptId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dept = ResultSetExtractHelper
						.extractDepartmentBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return dept;
	}
	
	@Override
	public Vector<DepartmentBean> getDepts(String deptName) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<DepartmentBean> vlist = new Vector<>();
		
		try {
			String additional = "";
			boolean flag = false;
			
			if (deptName != null && !deptName.trim().equals("")) {
				additional = "name like ? and ";
				flag = true;
			}
			String sql = "select * from dept_tbl where "
					+ additional + "id <> -1";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(1, '%' + deptName + '%');
			}
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractDepartmentBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
