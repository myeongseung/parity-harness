package service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.ResultSetExtractHelper;
import model.CompanyBean;
import repository.FieldCodeRepository;
import service.CompanyService;

public class CompanyServiceImpl implements CompanyService {
	private final FieldCodeRepository repository;
	
	private final DBConnectionServiceImpl pool;

	public CompanyServiceImpl() {
		repository = FieldCodeRepository.getInstance();
		
		pool = DBConnectionServiceImpl.getInstance();
	}

	@Override
	public boolean addCompany(HttpSession session, HttpServletRequest request) {
		boolean flag = false;

		if (session != null && request != null) {
			CompanyBean company = new CompanyBean();
			
			company.setId(Integer.parseInt(request.getParameter("id")));
			company.setName(request.getParameter("name"));
			company.setPostalCode(request.getParameter("postal_code"));
			company.setAddress1(request.getParameter("address1"));
			company.setAddress2(request.getParameter("address2"));
			company.setPhone(request.getParameter("phone"));
			company.setBusinessCode(request.getParameter("business_code"));
			company.setField(request.getParameter("field"));
			company.setBankCode(request.getParameter("bank_code"));
			company.setBankAccount(request.getParameter("bank_account"));
			company.setSubcontract(Integer.parseInt(request.getParameter("subcontract")));
			
			flag = addCompany(session, company);
		}
		return flag;
	}

	@Override
	public boolean addCompany(HttpSession session, CompanyBean company) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && company != null) {
			try {
				String sql = "insert into company_tbl values(0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				con = pool.getConnection();
				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, company.getName());
				pstmt.setString(2, company.getPostalCode());
				pstmt.setString(3, company.getAddress1());
				pstmt.setString(4, company.getAddress2());
				pstmt.setString(5, company.getPhone());
				pstmt.setString(6, company.getBusinessCode());
				pstmt.setString(7, company.getField());
				pstmt.setString(8, company.getBankCode());
				pstmt.setString(9, company.getBankAccount());
				pstmt.setInt(10, company.getSubcontract());

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
	public boolean deleteCompany(HttpSession session, String companyId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && companyId != null) {
			try {
				String sql = "delete from company_tbl where id = ?";
				con = pool.getConnection();
				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, companyId);

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
	public boolean updateCompany(HttpSession session, HttpServletRequest request) {
		boolean flag = false;

		if (session != null && request != null) {
			CompanyBean company = new CompanyBean();

			company.setId(Integer.parseInt(request.getParameter("id")));
			company.setName(request.getParameter("name"));
			company.setPostalCode(request.getParameter("postal_code"));
			company.setAddress1(request.getParameter("address1"));
			company.setAddress2(request.getParameter("address2"));
			company.setPhone(request.getParameter("phone"));
			company.setBusinessCode(request.getParameter("business_code"));
			company.setField(request.getParameter("field"));
			company.setBankCode(request.getParameter("bank_code"));
			company.setBankAccount(request.getParameter("bank_account"));
			company.setSubcontract(Integer.parseInt(request.getParameter("subcontract")));

			flag = updateCompany(session, company);
		}
		return flag;
	}

	@Override
	public boolean updateCompany(HttpSession session, CompanyBean company) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (session != null && company != null) {
			try {
				String sql = "update company_tbl set name = ?, postal_code = ?, address1 = ?, address2 = ?, "
						+ "phone = ?, business_code = ?,  field = ?, bank_code = ?, bank_account = ? , subcontract = ? where id = ?";
				con = pool.getConnection();
				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, company.getName());
				pstmt.setString(2, company.getPostalCode());
				pstmt.setString(3, company.getAddress1());
				pstmt.setString(4, company.getAddress2());
				pstmt.setString(5, company.getPhone());
				pstmt.setString(6, company.getBusinessCode());
				pstmt.setString(7, company.getField());
				pstmt.setString(8, company.getBankCode());
				pstmt.setString(9, company.getBankAccount());
				pstmt.setInt(10, company.getSubcontract());
				pstmt.setInt(11, company.getId());

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
	public CompanyBean getCompany(HttpSession session, String id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		CompanyBean bean = null;

		try {
			String sql = "select * from company_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean = ResultSetExtractHelper
						.extractCompanyBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}
	
	@Override
	public Vector<CompanyBean> getCompanies() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<CompanyBean> companies = new Vector<>();

		try {
			String sql = "select * from company_tbl order by id";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				companies.addElement(ResultSetExtractHelper
						.extractCompanyBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return companies;
	}

	@Override
	public Vector<CompanyBean> getCompanies(String keyfield, String keyword, int subcontract, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<CompanyBean> companies = new Vector<>();
		int index = 1;

		try {
			Vector<String> vlist = null;
			String additional = "";
			int flag = 0;
			con = pool.getConnection();
			
			if (keyfield.equals("name")) {
				additional = " and name like ?";
				flag = 1;
			} else if (keyfield.equals("field")) {
				StringBuilder sb = new StringBuilder();
				vlist = repository.getFieldCodes(keyword);
				
				for (int i = 0; i < vlist.size(); i++) {
					sb.append(",?");
				}
				String query = sb.toString().substring(1);
				additional = " and field in (" + query + ")";
				flag = 2;
				
			}
			String sql = "select * from company_tbl where subcontract = ?"
					+ additional + " order by id desc limit ?, ?";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(index++, subcontract);
			
			switch (flag) {
				case 1 -> {
					// 협력업체 명으로 조회
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					// 업체 종목별 조회
					for (String param : vlist) {
						pstmt.setString(index++, param);
					}
				}
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				companies.addElement(ResultSetExtractHelper
						.extractCompanyBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return companies;
	}

	@Override
	public int companyCount(String keyfield, String keyword, int subcontract) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;
		int index = 1;

		try {
			Vector<String> vlist = null;
			String additional = "";
			int flag = 0;
			con = pool.getConnection();
			
			if (keyfield.equals("name")) {
				additional = " and name like ?";
				flag = 1;
			} else if (keyfield.equals("field")) {
				StringBuilder sb = new StringBuilder();
				vlist = repository.getFieldCodes(keyword);
				
				for (int i = 0; i < vlist.size(); i++) {
					sb.append(",?");
				}
				String query = sb.toString().substring(1);
				additional = " and field in (" + query + ")";
				flag = 2;
				
			}
			String sql = "select count(*) from company_tbl where subcontract = ?"
					+ additional + " order by id desc";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(index++, subcontract);
			
			switch (flag) {
				case 1 -> {
					// 협력업체 명으로 조회
					pstmt.setString(index++, '%' + keyword + '%');
				}
				case 2 -> {
					// 업체 종목별 조회
					for (String param : vlist) {
						pstmt.setString(index++, param);
					}
				}
			}
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return result;
	}
}