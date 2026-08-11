package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import model.JobBean;
import service.JobService;

public class JobServiceImpl implements JobService {
	private final DBConnectionServiceImpl pool;
	
	private PermissionServiceImpl permissionSvc;
	
	public JobServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
		permissionSvc = new PermissionServiceImpl();
	}
	
	@Override
	public boolean createJob(HttpSession session, JobBean job) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if(session != null && job != null) {		
			if (permissionSvc.isAdmin(session)) {
				try {
					String sql = "insert into job_tbl (name) values (?)";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					
					pstmt.setString(1, job.getName());
					
					flag = pstmt.executeUpdate() == 1;
					
					try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							job.setId(generatedKeys.getInt(1));
			            } else {
			                throw new SQLException("Creating user failed, no ID obtained.");
			            }
					}
					pstmt.close();
					
					long permission = permissionSvc.next("job");
					sql = "insert into permission_job_tbl values (0, ?, ?, ?)";
					pstmt = con.prepareStatement(sql);
					
					pstmt.setInt(1, job.getId());
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
	public boolean deleteJob(HttpSession session, int jobId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if(session != null) {		
			if (permissionSvc.isAdmin(session)) {
				permissionSvc.deleteJobPermission(session, jobId);
				try {
					String sql = "delete from job_tbl where id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setInt(1, jobId);
					
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
	public boolean updateJob(HttpSession session, JobBean job) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if(session != null && job != null) {		
			if (permissionSvc.isAdmin(session)) {
				try {
					String sql = "update job_tbl set name = ? where id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setString(1, job.getName());
					pstmt.setInt(2, job.getId());
					
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
	public boolean hasJob(String name) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean flag = false;

		try {
			String sql = "select count(*) from job_tbl where name = ?";
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
	public JobBean getJob(int jobId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JobBean job = null;
		
		try {
			String sql = "select * from job_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, jobId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				job = new JobBean();
				job.setId(rs.getInt("id"));
				job.setName(rs.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return job;
	}
	
	@Override
	public Vector<JobBean> getJobs(String jobName) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<JobBean> jobs = new Vector<JobBean>();
		
		try {
			String additional = "";
			boolean flag = false;
			
			if (jobName != null && !jobName.trim().equals("")) {
				additional = "name like ? and ";
				flag = true;
			}
			String sql = "select * from job_tbl where "
					+ additional + "id <> -1";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			if (flag) {
				pstmt.setString(1, '%' + jobName + '%');
			}
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				JobBean job = new JobBean();
				job.setId(rs.getInt("id"));
				job.setName(rs.getString("name"));
				jobs.addElement(job);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return jobs;
	}
}
