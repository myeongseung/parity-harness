package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.TemplateBean;
import service.TemplateService;

public class TemplateServiceImpl implements TemplateService {
	private final DBConnectionServiceImpl pool;
	
	public TemplateServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}
	
	@Override
	public boolean createTemplate(HttpServletRequest request) {
		boolean result = false;
		
		if (request != null) {
			final String[] keys = { "subject", "content" };
			final HashMap<String, String> parameters = new HashMap<>();
			boolean isValid = true;
			
			for (String key : keys) {
				String value = request.getParameter(key);
				
				if (value == null) {
					isValid = false;
					break;
				}
				parameters.put(key, value);
			}
			if (isValid) {
				TemplateBean bean = new TemplateBean(
					-1,
					parameters.get("subject"),
					parameters.get("content")
				);
				
				result = createTemplate(bean);
			}
		}
		return result;
	}
	
	@Override
	public boolean createTemplate(TemplateBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "insert into template_tbl values (null, ?, ?)";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, bean.getSubject());
			pstmt.setString(2, bean.getContent());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean deleteTemplate(int templateId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "delete from template_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, templateId);

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean updateTemplate(HttpServletRequest request) {
		boolean result = false;
		
		if (request != null) {
			final String[] keys = { "id", "subject", "content" };
			final HashMap<String, String> parameters = new HashMap<>();
			boolean isValid = true;
			
			for (String key : keys) {
				String value = request.getParameter(key);
				
				if (value == null) {
					isValid = false;
					break;
				}
				parameters.put(key, value);
			}
			if (isValid) {
				int id = Integer.parseInt(parameters.get("id"));
				
				TemplateBean bean = getTemplate(id);
				
				bean.setSubject(parameters.get("subject"));
				bean.setContent(parameters.get("content"));
				
				result = updateTemplate(bean);
			}
		}
		return result;
	}
	
	@Override
	public boolean updateTemplate(TemplateBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		try {
			String sql = "update template_tbl set subject = ?, content = ? where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, bean.getSubject());
			pstmt.setString(2, bean.getContent());
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
	public TemplateBean getTemplate(int templateId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TemplateBean bean = null;

		try {
			String sql = "select * from template_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, templateId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				bean = new TemplateBean(
					rs.getInt("id"),
					rs.getString("subject"),
					rs.getString("content")
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}
	
	@Override
	public Vector<TemplateBean> getTemplates() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<TemplateBean> vlist = new Vector<>();

		try {
			String sql = "select * from template_tbl where id <> 0";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				TemplateBean bean = new TemplateBean(
					rs.getInt("id"),
					rs.getString("subject"),
					rs.getString("content")
				);
				vlist.addElement(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
