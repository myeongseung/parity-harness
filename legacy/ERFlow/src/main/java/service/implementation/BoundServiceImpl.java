package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import helper.ResultSetExtractHelper;
import helper.WebHelper;
import model.BoundBean;
import model.view.ViewBoundBean;
import service.BoundService;

public class BoundServiceImpl implements BoundService {
	private final DBConnectionServiceImpl pool;

	public BoundServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}

	@Override
	public boolean createInbound(HttpServletRequest request) {
		boolean result = false;

		if (request != null) {
			BoundBean bound = new BoundBean();

			bound.setProductId(request.getParameter("productId"));
			bound.setPostalCode(request.getParameter("postalCode"));
			bound.setAddress1(request.getParameter("address1"));
			bound.setAddress2(request.getParameter("address2"));
			bound.setBoundedAt(request.getParameter("boundedAt"));
			bound.setCount(Integer.parseInt(request.getParameter("count")));

			result = createInbound(bound);
		}
		return result;
	}

	@Override
	public boolean createInbound(BoundBean bound) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "insert into bound_tbl values(null , ?, ?, ?, ?, ?, ?, ?, 0)";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, bound.getProductId());
			pstmt.setString(2, bound.getUserId());
			pstmt.setString(3, bound.getPostalCode());
			pstmt.setString(4, bound.getAddress1());
			pstmt.setString(5, bound.getAddress2());
			pstmt.setString(6, bound.getBoundedAt());
			pstmt.setInt(7, bound.getCount());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean deleteInbound(int boundId) {
		Connection con = null;
		boolean flag = false;
		PreparedStatement pstmt = null;

		try {
			String sql = "delete from bound_tbl where id = ?  and type = 0";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boundId);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean updateInbound(HttpServletRequest request) {
		boolean flag = false;
		
		if (request != null) {
			BoundBean bound = new BoundBean();
			bound.setId(WebHelper.parseInt(request, "id"));
			bound.setProductId(request.getParameter("productID"));
			bound.setPostalCode(request.getParameter("postalcode"));
			bound.setAddress1(request.getParameter("address1"));
			bound.setAddress2(request.getParameter("address1"));
			bound.setCount(WebHelper.parseInt(request, "productID"));
			flag = updateInbound(bound);
		}
		return flag;
	}

	@Override
	public boolean updateInbound(BoundBean bound) {
		Connection con = null;
		boolean flag = false;
		PreparedStatement pstmt = null;

		try {
			String sql = "update bound_tbl set product_tbl_id = ?, postal_code = ?, address1 = ?, address2 = ?, count = ?, user_tbl_id = ? where id = ?  and type = 0";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, bound.getProductId());
			pstmt.setString(2, bound.getPostalCode());
			pstmt.setString(3, bound.getAddress1());
			pstmt.setString(4, bound.getAddress2());
			pstmt.setInt(5, bound.getCount());
			pstmt.setString(6, bound.getUserId());
			pstmt.setInt(7, bound.getId());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public ViewBoundBean getInbound(int boundId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ViewBoundBean bean = null;

		try {
			String sql = "select * from bound_view where id = ? and type = 0";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boundId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean = ResultSetExtractHelper
						.extractViewBoundBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	@Override
	public Vector<ViewBoundBean> getInbounds(String keyfield, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewBoundBean> inbounds = new Vector<>();

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "productName", "userName"
				};
			final String[] values = {
				"id", "product_name", "user_name"
			};
			String additional = "";
			int flag = 0;
			int index = 1;
			
			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (fields.containsKey(keyfield)) {
				additional = " and " + fields.get(keyfield) + " like ?";
				flag = 1;
			} else if (keyfield.equals("date")) {
				additional = " and bounded_at >= ?";
				flag = 2;
			} 
			String sql = "select * from bound_view where type = 0" + additional + " order by id limit ?, ?";
			pstmt = con.prepareStatement(sql);

			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setString(index++, keyword);
				}
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				inbounds.addElement(ResultSetExtractHelper
						.extractViewBoundBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return inbounds;
	}

	@Override
	public int getInboundCount(String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "productName", "userName"
				};
			final String[] values = {
				"id", "product_name", "user_name"
			};
			String additional = "";
			int flag = 0;
			int index = 1;
			
			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (fields.containsKey(keyfield)) {
				additional = " and " + fields.get(keyfield) + " like ?";
				flag = 1;
			} else if (keyfield.equals("date")) {
				additional = " and bounded_at >= ?";
				flag = 2;
			} 
			String sql = "select count(*) from bound_view where type = 0" + additional + " order by id";
			pstmt = con.prepareStatement(sql);

			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setString(index++, keyword);
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
	public boolean createOutbound(HttpServletRequest request) {
		boolean result = false;
		
		if (request != null) {
			BoundBean bound = new BoundBean();
			bound.setProductId(request.getParameter("productId"));
			bound.setPostalCode(request.getParameter("postalCode"));
			bound.setAddress1(request.getParameter("address1"));
			bound.setAddress2(request.getParameter("address2"));
			bound.setBoundedAt(request.getParameter("boundedAt"));
			bound.setCount(Integer.parseInt(request.getParameter("count")));

			result = createOutbound(bound);
		}
		return result;
	}

	@Override
	public boolean createOutbound(BoundBean bound) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "insert into bound_tbl values(null , ?, ?, ?, ?, ?, ?, ?, 1)";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, bound.getProductId());
			pstmt.setString(2, bound.getUserId());
			pstmt.setString(3, bound.getPostalCode());
			pstmt.setString(4, bound.getAddress1());
			pstmt.setString(5, bound.getAddress2());
			pstmt.setString(6, bound.getBoundedAt());
			pstmt.setInt(7, bound.getCount());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean deleteOutbound(int boundId) {
		Connection con = null;
		boolean flag = false;
		PreparedStatement pstmt = null;

		try {
			String sql = "delete from bound_tbl where id = ? and type = 1";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boundId);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean updateOutbound(HttpServletRequest request) {
		boolean flag = false;
		if (request != null) {
			BoundBean bound = new BoundBean();
			bound.setId(WebHelper.parseInt(request, "id"));
			bound.setProductId(request.getParameter("productID"));
			bound.setPostalCode(request.getParameter("postalcode"));
			bound.setAddress1(request.getParameter("address1"));
			bound.setAddress2(request.getParameter("address1"));
			bound.setCount(WebHelper.parseInt(request, "productID"));
			flag = updateInbound(bound);
		}
		return flag;
	}

	@Override
	public boolean updateOutbound(BoundBean bound) {
		Connection con = null;
		boolean flag = false;
		PreparedStatement pstmt = null;

		try {
			String sql = "update bound_tbl set product_tbl_id = ?, user_tbl_id = ?, postal_code = ?, address1 = ?, address2 = ?, count = ? where id = ?  and type = 1";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, bound.getProductId());
			pstmt.setString(2, bound.getUserId());
			pstmt.setString(3, bound.getPostalCode());
			pstmt.setString(4, bound.getAddress1());
			pstmt.setString(5, bound.getAddress2());
			pstmt.setInt(6, bound.getCount());
			pstmt.setInt(7, bound.getId());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public ViewBoundBean getOutbound(int boundId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ViewBoundBean bean = null;

		try {
			String sql = "select * from bound_view where id = ? and type = 1";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boundId);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				bean = ResultSetExtractHelper
						.extractViewBoundBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	@Override
	public Vector<ViewBoundBean> getOutbounds(String keyfield, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewBoundBean> outbounds = new Vector<>();

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "productName", "userName"
				};
			final String[] values = {
				"id", "product_name", "user_name"
			};
			String additional = "";
			int flag = 0;
			int index = 1;
			
			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (fields.containsKey(keyfield)) {
				additional = " and " + fields.get(keyfield) + " like ?";
				flag = 1;
			} else if (keyfield.equals("date")) {
				additional = " and bounded_at >= ?";
				flag = 2;
			} 
			String sql = "select * from bound_view where type = 1" + additional + " order by id limit ?, ?";
			pstmt = con.prepareStatement(sql);

			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setString(index++, keyword);
				}
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ViewBoundBean bean = ResultSetExtractHelper
						.extractViewBoundBean(rs);
				outbounds.addElement(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return outbounds;
	}

	@Override
	public int getOutboundCount(String keyfield, String keyword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"id", "productName", "userName"
				};
			final String[] values = {
				"id", "product_name", "user_name"
			};
			String additional = "";
			int flag = 0;
			int index = 1;
			
			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (fields.containsKey(keyfield)) {
				additional = " and " + fields.get(keyfield) + " like ?";
				flag = 1;
			} else if (keyfield.equals("date")) {
				additional = " and bounded_at >= ?";
				flag = 2;
			} 
			String sql = "select count(*) from bound_view where type = 1" + additional + " order by id";
			pstmt = con.prepareStatement(sql);

			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setString(index++, keyword);
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
	public BoundBean getBound(int boundId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoundBean bean = null;

		try {
			String sql = "select * from bound_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boundId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean =  ResultSetExtractHelper
						.extractBoundBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}
}