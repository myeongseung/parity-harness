package service.implementation;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import helper.WebHelper;
import model.UserBean;
import model.view.ViewProposalRouteBean;
import model.view.ViewUserBean;
import service.ProposalRouteService;

public class ProposalRouteServiceImpl implements ProposalRouteService {
	private final DBConnectionServiceImpl pool;
	private UserServiceImpl userSvc;

	public ProposalRouteServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
		userSvc = new UserServiceImpl();
	}

	@Override
	public boolean createProposalRoute(HttpServletRequest request) {
		ViewProposalRouteBean bean = new ViewProposalRouteBean();
		bean.setUserId(request.getParameter("userId"));
		bean.setUserName(request.getParameter("userName"));
		bean.setJobName(request.getParameter("jobName"));
		bean.setDeptName(request.getParameter("deptName"));
		bean.setNickname(request.getParameter("nickname"));
		String[] userIds = request.getParameterValues("route");
		Vector<ViewUserBean> users = new Vector<>();
		for (int i=0;i<users.size();i++) {
			ViewUserBean user = userSvc.getUserView(userIds[i]);
			users.addElement(user);
		}
		bean.setRoute(users);
		return createProposalRoute(bean);
	}

	@Override
	public boolean createProposalRoute(ViewProposalRouteBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		boolean flag = false;
		try {
			String sql = "insert into proposal_route_tbl values (null, ?, ?, ?, 0, now())";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, bean.getUserId());
			pstmt.setString(2, bean.getNickname());
			StringBuilder route = new StringBuilder();
	        for (int i = 0; i < bean.getRoute().size(); i++) {
	        	ViewUserBean user = bean.getRoute().get(i);
	            route.append(user.getId());
	            if (i < bean.getRoute().size() - 1) {
	                route.append(";");
	            }
	        }
	        String routeString = route.toString();
	        pstmt.setString(3, routeString);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean deleteProposalRoute(int proposalRouteId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "delete from proposal_route_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, proposalRouteId);
			
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean updateProposalRoute(HttpServletRequest request) {
		ViewProposalRouteBean bean = new ViewProposalRouteBean();
		bean.setId(WebHelper.parseInt(request, "id"));
		bean.setNickname(request.getParameter("id"));
		String[] userIds = request.getParameterValues("route");
		Vector<ViewUserBean> users = new Vector<>();
		for (int i=0;i<users.size();i++) {
			ViewUserBean user = userSvc.getUserView(userIds[i]);
			users.addElement(user);
		}
		bean.setRoute(users);
		return updateProposalRoute(bean);
	}

	@Override
	public boolean updateProposalRoute(ViewProposalRouteBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "update proposal_route_tbl set nickname = ?, route = ? where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, bean.getNickname());
			StringBuilder route = new StringBuilder();
	        for (int i = 0; i < bean.getRoute().size(); i++) {
	        	ViewUserBean user = bean.getRoute().get(i);
	            route.append(user.getId());
	            if (i < bean.getRoute().size() - 1) {
	                route.append(";");
	            }
	        }
	        String routeString = route.toString();
	        pstmt.setString(2, routeString);
			pstmt.setInt(3, bean.getId());
			
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public Vector<ViewProposalRouteBean> getProposalRoutes(String keyfield, String keyword, int start, int cnt, String userId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewProposalRouteBean> vlist = new Vector<>();

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "route"
				};
			final String[] values = {
				"id", "route"
			};
			String additional = "";
			int flag = 0;
			int index = 1;
			boolean idCheck = (userId != null || !userId.trim().equals(""));
			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (keyfield.equals("id")) {
				additional = " where " + fields.get(keyfield) + " = ?";
				flag = 1;
			} else if (keyfield.equals("route")) {
				additional = " where " + fields.get(keyfield) + " like ?";
				flag = 2;
			}
			if (idCheck) {
				additional += additional.contains("where") ? " and user_id = ?" : " where user_id = ?";
			}
			String sql = "select * from proposal_route_view" + additional + " order by id limit ?, ?";
			pstmt = con.prepareStatement(sql);
			
			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, keyword);
				}
				case 2 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
			}
			if (idCheck) {
				pstmt.setString(index++, userId );
			}
			
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ViewProposalRouteBean bean = new ViewProposalRouteBean();
				bean.setId(rs.getInt("id"));
				bean.setUserId(rs.getString("user_id"));
				bean.setUserName(rs.getString("user_name"));
				bean.setJobName(rs.getString("job_name"));
				bean.setDeptName(rs.getString("dept_name"));
				bean.setNickname(rs.getString("nickname"));
				String[] route = {};
				if (rs.getString("route") != null) {
					route  = rs.getString("route").split(";");
				}
				Vector<ViewUserBean> users = new Vector<>();
				for (int i=0; i < route.length;i++) {
					ViewUserBean user = userSvc.getUserView(route[i]);
					users.addElement(user);
				}
				bean.setRoute(users);
				bean.setType(rs.getInt("type"));
				bean.setCreatedAt(rs.getString("created_at"));
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
	public int getProposalRoutesCount(String keyfield, String keyword, String userId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "route"
				};
			final String[] values = {
				"id", "route"
			};
			String additional = "";
			int flag = 0;
			int index = 1;
			boolean idCheck = (userId != null || !userId.trim().equals(""));
			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (keyfield.equals("Id")) {
				additional = " where " + fields.get(keyfield) + " = ?";
				flag = 1;
			} else if (keyfield.equals("User") || keyfield.equals("Route")) {
				additional = " where " + fields.get(keyfield) + " like ?";
				flag = 2;
			}
			if (idCheck) {
				additional += additional.contains("where") ? " and user_id = ?" : " where user_id = ?";
			}
			String sql = "select count(*) from proposal_route_view" + additional;
			pstmt = con.prepareStatement(sql);
			
			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, keyword);
				}
				case 2 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
			}
			if (idCheck) {
				pstmt.setString(index++, userId );
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
	public ViewProposalRouteBean getProposalRouteView(int proposalRouteId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ViewProposalRouteBean bean = null;
		
		try {
			String sql = "select * from proposal_route_view where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, proposalRouteId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				bean = new ViewProposalRouteBean();
				bean.setId(rs.getInt("id"));
				bean.setUserId(rs.getString("user_id"));
				bean.setUserName(rs.getString("user_name"));
				bean.setJobName(rs.getString("job_name"));
				bean.setDeptName(rs.getString("dept_name"));
				bean.setNickname(rs.getString("nickname"));
				String[] route = rs.getString("route").split(";");
				Vector<ViewUserBean> users = new Vector<>();
				for (int i=0; i<route.length;i++) {
					ViewUserBean user = userSvc.getUserView(route[i]);
					users.addElement(user);
				}
				bean.setRoute(users);
				bean.setType(rs.getInt("type"));
				bean.setCreatedAt(rs.getString("created_at"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}
}
