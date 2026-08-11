package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import helper.ResultSetExtractHelper;
import helper.WebHelper;
import model.ProposalBean;
import model.view.ViewProposalBean;
import model.view.ViewProposalRouteBean;
import model.view.ViewUserBean;
import service.ProposalService;

public class ProposalServiceImpl implements ProposalService {
	private final DBConnectionServiceImpl pool;
	private final ProposalRouteServiceImpl routeSvc;
	
	public ProposalServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
		routeSvc = new ProposalRouteServiceImpl();
	}

	@Override
	public boolean createProposal(HttpServletRequest request) {
		ProposalBean bean = new ProposalBean();
		bean.setUserId(request.getParameter("userId"));
		bean.setDocumentId(WebHelper.parseLong(request, "documentId"));
		bean.setRouteId(WebHelper.parseInt(request, "routeId"));
		return createProposal(bean);
	}

	@Override
	public boolean createProposal(ProposalBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "insert into proposal_tbl values (null, ?, ?, ?, ?, 3, null, now(), null)";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, bean.getDocumentId());
			pstmt.setString(2, bean.getUserId());
			pstmt.setInt(3, bean.getRouteId());
			pstmt.setInt(4, bean.getStep());
			
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean confirmProposal(ProposalBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "update proposal_tbl set result = 0, approved_at = now(), comment = ? where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, bean.getComment());
			pstmt.setLong(2, bean.getId());
			
			flag= pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean confirmProposals(ProposalBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "update proposal_tbl set result = 1 where document_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, bean.getDocumentId());
			
			flag= pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean isFinalStep(ProposalBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		ResultSet rs = null;
		
		try {
			String sql = "select * from proposal_view where document_id = ? order by step desc limit 1";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, bean.getDocumentId());
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				if (rs.getInt("step") == rs.getString("route").split(";").length - 1) {
					flag = true;
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
	public boolean rejectProposal(ProposalBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "update proposal_tbl set comment = ? where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, bean.getComment());
			pstmt.setLong(2, bean.getId());
			
			flag= pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean rejectProposals(ProposalBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "update proposal_tbl set result = 2 where document_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, bean.getDocumentId());
			
			flag= pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean hasProposal(long documentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean flag = false;
		
		try {
			String sql = "select * from proposal_tbl where document_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, documentId);

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				flag = documentId == rs.getLong("document_tbl_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public ProposalBean getProposal(long proposalId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ProposalBean bean = null;
		try {
			String sql = "select * from proposal_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, proposalId);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				bean = ResultSetExtractHelper.extractProposalBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	@Override
	public ViewProposalBean getProposalView(long proposalId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ViewProposalBean bean = null;
		
		try {
			String sql = "select * from proposal_view where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, proposalId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				bean = ResultSetExtractHelper.extractViewProposalBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	@Override
	public Vector<ViewProposalBean> getProposalViews() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewProposalBean> vlist = new Vector<>();
		
		try {
			String sql = "select * from proposal_view";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractViewProposalBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}

	@Override
	public Vector<ViewProposalBean> getProposalViews(long documentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewProposalBean> vlist = new Vector<>();
		
		try {
			String sql = "select * from proposal_view where document_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, documentId);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractViewProposalBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ViewProposalBean> getProposalViews(String userId, int result, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewProposalBean> vlist = new Vector<>();
		
		try {
			String additional = result == 0 ? "and approved_at is null " : "";
			String sql = "select * from proposal_view where (result = 3 and route like ?) "
					+ "or (original_user = ? and user_id= ? and result = ?) " + additional
					+ " order by id desc limit ?, ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, '%' + userId + '%');
			pstmt.setString(2, userId);
			pstmt.setString(3, userId);
			pstmt.setInt(4, result);
			pstmt.setInt(5, start);
			pstmt.setInt(6, cnt);
			
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractViewProposalBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist.stream().filter(bean -> {
			ViewProposalRouteBean routeBean = routeSvc.getProposalRouteView(bean.getRouteId());
			Vector<ViewUserBean> users = routeBean.getRoute();
			int step = bean.getStep();
			
			return users.size() > step && users.get(step).getId().equals(userId);
		}).collect(Collectors.toCollection(Vector::new));
	}
	

	@Override
	public int getProposalViewsCount(String userId, int result) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		
		try {
			String additional = result == 0 ? "and approved_at is null " : "";
			String sql = "select count(*) from proposal_view where (result = 3 and route like ?) "
					+ "or (original_user = ? and user_id= ? and result = ?) " + additional;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, '%' + userId + '%');
			pstmt.setString(2, userId);
			pstmt.setString(3, userId);
			pstmt.setInt(4, result);
			
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
	public Vector<ViewProposalBean> getRecentProposalViews() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewProposalBean> vlist = new Vector<>();
		
		try {
			String sql = "select * from recent_proposal_view order by id desc";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractViewProposalBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ViewProposalBean> getRecentProposalViews(String userId, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewProposalBean> vlist = new Vector<>();
		
		try {
			String sql = "select * from recent_proposal_view where original_user = ? or "
					+ "(user_id = ? and result = 3) order by id desc limit ?, ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setString(2, userId);
			pstmt.setInt(3, start);
			pstmt.setInt(4, cnt);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper.extractViewProposalBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
