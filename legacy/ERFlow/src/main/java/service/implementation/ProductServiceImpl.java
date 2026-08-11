package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.ResultSetExtractHelper;
import helper.WebHelper;
import model.ProductBean;
import service.ProductService;

public class ProductServiceImpl implements ProductService {
	private final DBConnectionServiceImpl pool;
	
	private PermissionServiceImpl permissionSvc;
	
	public ProductServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
		permissionSvc = new PermissionServiceImpl();
	}
	
	/*
	 * 
	 * */

	@Override
	public boolean createProduct(HttpServletRequest request) {
		ProductBean bean = new ProductBean();
		bean.setId(request.getParameter("productId"));
		bean.setName(request.getParameter("productName"));
		bean.setCount(WebHelper.parseInt(request, "count"));
		bean.setType(WebHelper.parseInt(request, "type"));
		return createProduct(bean);
	}

	@Override
	public boolean createProduct(ProductBean product) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		int index = 1;
		try {
			String sql = "insert into product_tbl values(?, ?, ?, ?)";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(index++, product.getId());
			pstmt.setString(index++, product.getName());
			pstmt.setInt(index++, product.getCount());
			pstmt.setInt(index++, product.getType());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean deleteProduct(String productId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "delete from product_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, productId);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public boolean updateProduct(HttpServletRequest request) {
		ProductBean bean = new ProductBean();
		bean.setId(request.getParameter("productId"));
		bean.setName(request.getParameter("productName"));
		bean.setCount(WebHelper.parseInt(request, "count"));
		bean.setType(WebHelper.parseInt(request, "type"));
		return updateProduct(bean);
	}

	@Override
	public boolean updateProduct(ProductBean product) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		int index = 1;
		try {
			String sql = "update product_tbl set name = ?, count = ?, type = ? where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(index++, product.getName());
			pstmt.setInt(index++, product.getCount());
			pstmt.setInt(index++, product.getType());
			pstmt.setString(index++, product.getId());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	@Override
	public ProductBean getProduct(String productId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ProductBean product  = null;
		try {
			String sql = "select * from product_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, productId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				product = new ProductBean();
				product.setId(rs.getString("id"));
				product.setName(rs.getString("name"));
				product.setCount(rs.getInt("count"));
				product.setType(rs.getInt("type"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return product;
	}

	@Override
	public Vector<ProductBean> getProducts(String keyfield, String keyword, int start, int cnt, String type) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ProductBean> products = new Vector<>();

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = {
				"productId", "productName"
				};
			final String[] values = {
				"id", "name", "type"
			};
			final HashMap<String, Integer> types = new HashMap<>();
			final String[] typekeys = {
				"ingredient", "processed", "producted"
				};
			final Integer[] typevalues = {
				0,1,2
			};
			String additional = "";
			int flag = 0;
			int index = 1;
			
			for (int i = 0; i < typekeys.length; ++i) {
				types.put(typekeys[i], typevalues[i]);
			}
			
			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (fields.containsKey(keyfield)) {
				additional = "and " + fields.get(keyfield) + " like ? ";
				flag = 1;
			} 
			String sql = "select * from product_tbl where type = ? " + additional + "order by id limit ?, ?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(index++,types.get(type));
			
			switch (flag) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				ProductBean product = new ProductBean();
				product.setId(rs.getString("id"));
				product.setName(rs.getString("name"));
				product.setCount(rs.getInt("count"));
				product.setType(rs.getInt("type"));
				products.addElement(product);
			}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				pool.freeConnection(con, pstmt, rs);
			}
			return products;
	}

	@Override
	public int getProductCount(String keyfield, String keyword, String type) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			final HashMap<String, String> fields = new HashMap<>();
			final String[] keys = { "productId", "productName" };
			final String[] values = { "id", "name", "type" };
			final HashMap<String, Integer> types = new HashMap<>();
			final String[] typekeys = { "ingredient", "processed", "producted" };
			final Integer[] typevalues = { 0, 1, 2 };
			String additional = "";
			int flag = 0;
			int index = 1;

			for (int i = 0; i < typekeys.length; ++i) {
				types.put(typekeys[i], typevalues[i]);
			}

			for (int i = 0; i < keys.length; ++i) {
				fields.put(keys[i], values[i]);
			}
			con = pool.getConnection();

			if (fields.containsKey(keyfield)) {
				additional = "and " + fields.get(keyfield) + " like ? ";
				flag = 1;
			}
			String sql = "select count(*) from product_tbl where type = ? " + additional;
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(index++, types.get(type));

			switch (flag) {
			case 1 -> {
				pstmt.setString(index++, '%' + keyword + '%');
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
	public Vector<ProductBean> getAllProducts() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ProductBean> vlist = new Vector<ProductBean>();
		try {
			String sql = "select * from product_tbl";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				ProductBean product = new ProductBean();
				product.setId(rs.getString("id"));
				product.setName(rs.getString("name"));
				product.setCount(rs.getInt("count"));
				product.setType(rs.getInt("type"));
				vlist.addElement(product);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
