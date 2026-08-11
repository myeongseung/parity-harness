package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import helper.ResultSetExtractHelper;
import model.DocumentBean;
import model.view.ViewDocumentBean;
import service.DocumentService;

public class DocumentServiceImpl implements DocumentService {
	private final DBConnectionServiceImpl pool;
	
	public DocumentServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}
	
	@Override
	public boolean changeStatus(long documentId, int status) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "update document_tbl set doc_status = ? where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, status);
			pstmt.setLong(2, documentId);

			flag = pstmt.executeUpdate() ==1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean createDocument(DocumentBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		if (bean != null) {
			try {
				String sql = "insert into document_tbl values "
						+ "(null, ?, ?, ?, ?, ?, 0, 0, now(), null)";
				con = pool.getConnection();
				pstmt = con.prepareStatement(sql);
	
				pstmt.setString(1, bean.getUserId());
				pstmt.setInt(2, bean.getRouteId());
				pstmt.setInt(3, bean.getTemplateId());
				pstmt.setString(4, bean.getSubject());
				pstmt.setString(5, bean.getContent());
				
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
	public boolean createDocument(HttpServletRequest request) {
		boolean result = false;
		
		if (request != null) {
			final String[] keys = { "userId", "routeId", "templateId", "subject", "bodyContent" };
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
			// 매개변수 검증이 완료되었으면,
			if (isValid) {
				DocumentBean bean = new DocumentBean(
					-1L,
					parameters.get("userId"),
					Integer.parseInt(parameters.get("routeId")),
					Integer.parseInt(parameters.get("templateId")),
					parameters.get("subject"),
					parameters.get("bodyContent"),
					0,
					0,
					null,
					null
				);
				result = createDocument(bean);
			}
		}
		return result;
	}
	
	@Override
	public boolean deleteDocument(long documentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "delete from document_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, documentId);

			flag = pstmt.executeUpdate() == 1;
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
			String sql = "select count(*) from proposal_tbl where document_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, documentId);
			
			rs = pstmt.executeQuery();
			
			flag = rs.next() && rs.getInt(1) >= 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return flag;
	}

	@Override
	public boolean updateDocument(DocumentBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "update document_tbl set proposal_route_tbl_id = ?, "
					+ "template_tbl_id = ?, subject = ?, content = ?, updated_at = now() where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, bean.getRouteId());
			pstmt.setInt(2, bean.getTemplateId());
			pstmt.setString(3, bean.getSubject());
			pstmt.setString(4, bean.getContent());
			pstmt.setLong(5, bean.getId());

			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean updateDocument(HttpServletRequest request) {
		boolean result = false;
		
		if (request != null) {
			final String[] keys = { "id", "userId", "routeId", "templateId", "subject", "bodyContent" };
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
			// 매개변수 검증이 완료되었으면,
			if (isValid) {
				DocumentBean bean = new DocumentBean(
					Long.parseLong(parameters.get("id")),
					parameters.get("userId"),
					Integer.parseInt(parameters.get("routeId")),
					Integer.parseInt(parameters.get("templateId")),
					parameters.get("subject"),
					parameters.get("bodyContent"),
					0,
					0,
					null,
					null
				);
				result = updateDocument(bean);
			}
		}
		return result;
	}
	
	@Override
	public int getDocumentCount(String keyfield, String keyword, String flag, String value) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;

		try {
			String additional = "";
			String conditional = "";
			int index = 1;
			int type = 0;
			
			if (keyfield != null && !keyfield.trim().equals("") &&
				keyword != null && !keyword.trim().equals("")) {
				additional += " where ";
				
				if (keyfield.equals("subject")) {
					additional += "subject like ?";
					type = 1;
				} else if (keyfield.equals("template")) {
					additional += "template_name like ?";
					type = 1;
				} else if (keyfield.equals("id")){
					additional += "id = ?";
					type = 2;
				}
			}
			if (flag != null) {
				if (type > 0) {
					conditional += " and ";
				} else {
					conditional += " where ";
				}
				switch (flag) {
					case "dept" -> {
						conditional += "dept_name = ?";
					}
					case "user" -> {
						conditional += "user_id = ?";
					}
				}
			}
			String sql = "select count(*) from document_view" +
					additional + conditional + " order by id desc";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			switch (type) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setLong(index++, Long.parseLong(keyword));
				}
			}
			if (!conditional.equals("")) {
				pstmt.setString(index++, value);
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
	public DocumentBean getDocument(long documentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DocumentBean bean = null;

		try {
			String sql = "select * from document_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, documentId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean = ResultSetExtractHelper
						.extractDocumentBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}
	
	@Override
	public ViewDocumentBean getDocumentView(long documentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ViewDocumentBean bean = null;

		try {
			String sql = "select * from document_view where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setLong(1, documentId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean = ResultSetExtractHelper
						.extractViewDocumentBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}
	
	@Override
	public Vector<DocumentBean> getDocuments(String keyfield, String keyword, String flag, String value, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<DocumentBean> vlist = new Vector<>();

		try {
			String additional = "";
			String conditional = "user_id = ?";
			int index = 1;
			int type = 0;
			
			switch (flag) {
				case "dept" -> {
					conditional = "dept_name = ?";
				}
			}
			if (keyfield.equals("subject") || keyfield.equals("content")) {
				additional = keyfield + " like ?";
				type = 1;
			}
			String sql = "select * from document_tbl where " +
					additional + conditional + " order by id desc limit ?,?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			switch (type) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
			}
			pstmt.setString(index++, value);
			pstmt.setInt(index++, start);			
			pstmt.setInt(index++, cnt);			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractDocumentBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ViewDocumentBean> getDocumentViews() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewDocumentBean> vlist = new Vector<>();

		try {
			String sql = "select * from document_view";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewDocumentBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ViewDocumentBean> getDocumentViews(String keyfield, String keyword, String flag, String value) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewDocumentBean> vlist = new Vector<>();

		try {
			String additional = "";
			String conditional = "";
			int index = 1;
			int type = 0;
			
			if (keyfield != null && !keyfield.trim().equals("") &&
				keyword != null && !keyword.trim().equals("")) {
				additional += " where ";
				
				if (keyfield.equals("subject")) {
					additional += "subject like ?";
					type = 1;
				} else if (keyfield.equals("template")) {
					additional += "template_name like ?";
					type = 1;
				} else if (keyfield.equals("id")){
					additional += "id = ?";
					type = 2;
				}
			}
			if (flag != null) {
				if (type > 0) {
					conditional += " and ";
				} else {
					conditional += " where ";
				}
				switch (flag) {
					case "dept" -> {
						conditional += "dept_name = ?";
					}
					case "user" -> {
						conditional += "user_id = ?";
					}
				}
			}
			String sql = "select * from document_view" +
					additional + conditional + " order by id desc";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			switch (type) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setLong(index++, Long.parseLong(keyword));
				}
			}
			if (!conditional.equals("")) {
				pstmt.setString(index++, value);
			}
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewDocumentBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
	
	@Override
	public Vector<ViewDocumentBean> getDocumentViews(String keyfield, String keyword, String flag, String value, int start,
			int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewDocumentBean> vlist = new Vector<>();

		try {
			String additional = "";
			String conditional = "";
			int index = 1;
			int type = 0;
			
			if (keyfield != null && !keyfield.trim().equals("") &&
				keyword != null && !keyword.trim().equals("")) {
				additional += " where ";
				
				if (keyfield.equals("subject")) {
					additional += "subject like ?";
					type = 1;
				} else if (keyfield.equals("template")) {
					additional += "template_name like ?";
					type = 1;
				} else if (keyfield.equals("id")){
					additional += "id = ?";
					type = 2;
				}
			}
			if (flag != null) {
				if (type > 0) {
					conditional += " and ";
				} else {
					conditional += " where ";
				}
				switch (flag) {
					case "dept" -> {
						conditional += "dept_name = ?";
					}
					case "user" -> {
						conditional += "user_id = ?";
					}
				}
			}
			String sql = "select * from document_view" +
					additional + conditional + " order by id desc limit ?, ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			switch (type) {
				case 1 -> {
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					pstmt.setLong(index++, Long.parseLong(keyword));
				}
			}
			if (!conditional.equals("")) {
				pstmt.setString(index++, value);
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewDocumentBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
