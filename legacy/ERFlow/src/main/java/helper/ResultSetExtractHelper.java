package helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import model.BoardBean;
import model.BoundBean;
import model.CommentBean;
import model.CompanyBean;
import model.DepartmentBean;
import model.DocumentBean;
import model.FileBean;
import model.MessageBean;
import model.PostBean;
import model.ProcessBean;
import model.ProgramBean;
import model.ProposalBean;
import model.TaskBean;
import model.TaskHistoryBean;
import model.UnitBean;
import model.UserBean;
import model.view.ViewBoundBean;
import model.view.ViewCalendarBean;
import model.view.ViewCommentBean;
import model.view.ViewDocumentBean;
import model.view.ViewMessageBean;
import model.view.ViewPermissionBean;
import model.view.ViewPostBean;
import model.view.ViewProposalBean;
import model.view.ViewTaskBean;
import model.view.ViewUnitBean;
import model.view.ViewUserBean;
import model.view.ViewWorkBean;

public class ResultSetExtractHelper {
	public static BoardBean extractBoardBean(ResultSet rs)
			throws SQLException {
		return new BoardBean(
			rs.getInt("id"),
			rs.getString("subject"),
			rs.getString("created_at"),
			rs.getLong("permission_read_dept_level"),
			rs.getLong("permission_read_job_level"),
			rs.getLong("permission_write_dept_level"),
			rs.getLong("permission_write_job_level")
		);
	}
	
	public static BoundBean extractBoundBean(ResultSet rs)
	         throws SQLException {
	      return new BoundBean(
	         rs.getInt("id"),
	         rs.getString("product_tbl_id"),
	         rs.getString("user_tbl_id"),
	         rs.getString("postal_code"),
	         rs.getString("address1"),
	         rs.getString("address2"),
	         rs.getString("bounded_at"),
	         rs.getInt("count"),
	         rs.getInt("type")
	      );
	   }
	
	public static CommentBean extractCommentBean(ResultSet rs)
	         throws SQLException {
	      return new CommentBean(
	         rs.getInt("id"),
	         rs.getInt("post_tbl_id"),
	         rs.getInt("comment_tbl_ref_id"),
	         rs.getString("user_tbl_id"),
	         rs.getString("comment"),
	         rs.getInt("depth"),
	         rs.getString("created_at")
	      );
	   }

	
	public static CompanyBean extractCompanyBean(ResultSet rs)
			throws SQLException {
		return new CompanyBean(
			rs.getInt("id"),
			rs.getString("name"),
			rs.getString("postal_code"),
			rs.getString("address1"),
			rs.getString("address2"),
			rs.getString("phone"),
			rs.getString("business_code"),
			rs.getString("field"),
			rs.getString("bank_code"),
			rs.getString("bank_account"),
			rs.getInt("subcontract")
		);
	}
	
	public static DepartmentBean extractDepartmentBean(ResultSet rs)
			throws SQLException {
		return new DepartmentBean(
			rs.getInt("id"),
			rs.getString("user_tbl_manager_id"),
			rs.getString("postal_code"),
			rs.getString("address1"),
			rs.getString("address2"),
			rs.getString("name")
		);
	}
	
	public static DocumentBean extractDocumentBean(ResultSet rs)
			throws SQLException {
		return new DocumentBean(
			rs.getLong("id"),
			rs.getString("user_tbl_id"),
			rs.getInt("proposal_route_tbl_id"),
			rs.getInt("template_tbl_id"),
			rs.getString("subject"),
			rs.getString("content"),
			rs.getInt("doc_status"),
			rs.getInt("proposal_status"),
			rs.getString("created_at"),
			rs.getString("updated_at")
		);
	}
	
	public static MessageBean extractMessageBean(ResultSet rs) 
			throws SQLException {
		return new MessageBean(
			rs.getInt("id"),
			rs.getString("user_tbl_sender_id"),
			rs.getString("user_tbl_receiver_id"),
			rs.getString("content"),
			rs.getString("created_at"),
			rs.getString("read_at"),
			rs.getInt("read_status"),
			rs.getInt("sender_visible"),
			rs.getInt("receiver_visible")
		);
	}
	
	public static PostBean extractPostBean(ResultSet rs)
			throws SQLException {
		return new PostBean(
			rs.getInt("id"),
			rs.getString("user_tbl_id"),
			rs.getInt("board_tbl_id"),
			rs.getInt("post_tbl_ref_id"),
			rs.getString("subject"),
			rs.getString("content"),
			rs.getInt("depth"),
			rs.getInt("pos"),
			rs.getInt("count"),
			rs.getInt("delete"),
			rs.getString("created_at")
		);
	}
	
	public static FileBean extractPostFileBean(ResultSet rs)
			throws SQLException {
		return new FileBean(
			rs.getInt("id"),
			rs.getInt("post_tbl_id"),
			rs.getString("original_name"),
			rs.getString("name"),
			rs.getString("extension"),
			rs.getLong("size")
		);
	}

	public static ProgramBean extractProgramBean(ResultSet rs)
			throws SQLException{
		return new ProgramBean(
			rs.getInt("id"),
			rs.getString("program_id"),
			rs.getString("program_name"),
			rs.getLong("dept_level"),
			rs.getLong("job_level")
		);
	}
	
	public static ProposalBean extractProposalBean(ResultSet rs) throws SQLException {
		return new ProposalBean(
			rs.getLong("id"),
			rs.getLong("document_tbl_id"),
			rs.getString("user_tbl_id"),
			rs.getInt("proposal_route_tbl_id"),
			rs.getInt("step"), rs.getInt("result"),
			rs.getString("result"),
			rs.getString("received_at"),
			rs.getString("approved_at")
		);
	}
	
	public static ProcessBean extractProcessBean(ResultSet rs)
	         throws SQLException {
	            return new ProcessBean(
	               rs.getString("id"),
	               rs.getString("process_tbl_prev_id"),
	               rs.getString("process_tbl_next_id"),
	               rs.getString("name"),
	               rs.getInt("priority")
	            );
	         }
	
	public static TaskBean extractTaskBean(ResultSet rs)
	         throws ParseException, SQLException {
		return new TaskBean(
			rs.getInt("id"),
			rs.getString("user_tbl_id"),
			rs.getInt("company_tbl_id"),
			rs.getInt("document_tbl_id"),
			rs.getInt("type"),
			rs.getString("task_at"),
			WebHelper.getDate(rs.getString("created_at")),
			rs.getInt("status")
		);
	}
	
	public static TaskHistoryBean extractTaskHistoryBean(ResultSet rs)
			throws SQLException {
		return new TaskHistoryBean(
			rs.getInt("task_tbl_id"),
			rs.getString("product_tbl_id"),
			rs.getInt("count")
		);
	}
	
	public static UnitBean extractUnitBean(ResultSet rs)
			throws SQLException {
		return new UnitBean(
			rs.getString(1),
			rs.getString(2),
			rs.getInt(3),
			rs.getString(4),
			rs.getInt(5),
			rs.getString(6)
		);
	}
	
	public static ViewBoundBean extractViewBoundBean(ResultSet rs)
	         throws SQLException {
	      return new ViewBoundBean(
	         rs.getInt("id"),
	         rs.getString("product_id"),
	         rs.getString("product_name"),
	         rs.getString("product_id"),
	         rs.getString("user_name"),
	         rs.getString("postal_code"),
	         rs.getString("address1"),
	         rs.getString("address2"),
	         rs.getString("bounded_at"),
	         rs.getInt("count"),
	         rs.getInt("type")
	      );
	   }
	
	public static UserBean extractUserBean(ResultSet rs)
			throws SQLException {
		return new UserBean(
			rs.getString("id"),
			rs.getInt("job_tbl_id"),
			rs.getInt("dept_tbl_id"),
			rs.getString("user_tbl_manager_id"),
			rs.getInt("salary_tbl_id"),
			rs.getString("name"),
			rs.getString("password"),
			rs.getString("social_number"),
			rs.getString("postal_code"),
			rs.getString("address1"),
			rs.getString("address2"),
			rs.getString("extension_phone"),
			rs.getString("mobile_phone"),
			rs.getString("email"),
			rs.getString("hired_at"),
			rs.getString("left_at")
		);
	}
	
	public static ViewCommentBean extractViewCommentBean(ResultSet rs)
			throws SQLException {
		return new ViewCommentBean(
			rs.getInt("id"),
			rs.getString("user_id"),
			rs.getString("name"),
			rs.getInt("post_id"),
			rs.getString("subject"),
			rs.getInt("ref_id"),
			rs.getString("comment"),
			rs.getInt("depth"),
			rs.getString("created_at")
		);
	}
	
	public static ViewDocumentBean extractViewDocumentBean(ResultSet rs)
			throws SQLException {
		return new ViewDocumentBean(
			rs.getLong("id"),
			rs.getString("subject"),
			rs.getString("content"),
			rs.getString("template_name"),
			rs.getString("created_at"),
			rs.getString("updated_at"),
			rs.getString("user_id"),
			rs.getString("name"),
			rs.getString("dept_name"),
			rs.getString("job_name"),
			rs.getString("route_title"),
			rs.getInt("route_id"),
			rs.getString("route_name"),
			rs.getInt("doc_status"),
			rs.getInt("proposal_status")
		);
	}
	
	public static ViewMessageBean extractViewMessageBean(ResultSet rs) throws SQLException {
		return new ViewMessageBean(
			rs.getInt("id"),
			rs.getString("receiver_id"),
			rs.getString("receiver_name"),
			rs.getString("receiver_dept_name"),
			rs.getString("receiver_job_name"),
			rs.getString("sender_id"),
			rs.getString("sender_name"),
			rs.getString("sender_dept_name"),
			rs.getString("sender_job_name"),
			rs.getString("content"),
			rs.getString("created_at"),
			rs.getString("read_at"),
			rs.getInt("read_status"),
			rs.getInt("sender_visible"),
			rs.getInt("receiver_visible")
		);
	}
	
	public static ViewPostBean extractViewPostBean(ResultSet rs)
			throws SQLException {
		return new ViewPostBean(
			rs.getInt("id"),
			rs.getInt("board_id"),
			rs.getString("user_id"),
			rs.getString("name"),
			rs.getInt("ref_id"),
			rs.getString("subject"),
			rs.getString("content"),
			rs.getInt("depth"),
			rs.getInt("pos"),
			rs.getInt("count"),
			rs.getString("created_at"),
			rs.getInt("delete")
		);
	}
	
	public static ViewProposalBean extractViewProposalBean(ResultSet rs) throws SQLException {
		return new ViewProposalBean(
			rs.getLong("id"),
			rs.getLong("document_id"),
			rs.getString("subject"),
			rs.getString("content"),
			rs.getString("user_id"),
			rs.getInt("step"),
			rs.getInt("result"),
			rs.getString("comment"),
			rs.getString("received_at"),
			rs.getString("approved_at"),
			rs.getString("nickname"),
			rs.getInt("route_id"),
			rs.getString("route")
		);
	}
	
	
	public static ViewTaskBean extractViewtTaskBean(ResultSet rs)
	         throws ParseException, SQLException {
		return new ViewTaskBean(
			rs.getInt("task_id"),
			rs.getString("user_id"),
			rs.getString("user_name"),
			rs.getString("dept_name"),
			rs.getInt("company_id"),
			rs.getString("company_name"),
			rs.getInt("document_id"),
			rs.getString("subject"),
			rs.getInt("type"),
			rs.getString("task_at"),
			WebHelper.getDate(rs.getString("created_at")),
			rs.getInt("status"),
			rs.getString("products")
		);
	}
   
	public static ViewPermissionBean extractViewDeptPermissionBean(ResultSet rs)
			throws SQLException {
		return new ViewPermissionBean(
			rs.getInt("id"),
			rs.getInt("dept_id"),
			rs.getString("name"),
			rs.getLong("level"),
			rs.getLong("permission")
		);
	}
	
	public static ViewPermissionBean extractViewJobPermissionBean(ResultSet rs)
			throws SQLException {
		return new ViewPermissionBean(
			rs.getInt("id"),
			rs.getInt("job_id"),
			rs.getString("name"),
			rs.getLong("level"),
			rs.getLong("permission")
		);
	}
	
	public static ViewCalendarBean extractCalendarViewBean(ResultSet rs)
            throws SQLException {
        return new ViewCalendarBean(
            rs.getInt("id"),
            rs.getString("user_id"),
            rs.getInt("dept_id"),
            rs.getString("dept_name"),
            rs.getString("subject"),
            rs.getString("content"),
            rs.getString("started_at"),
            rs.getString("ended_at"),
            rs.getInt("type")
        );
    }
	
	public static ViewUnitBean extractViewUnitBean(ResultSet rs)
			throws SQLException {
		return new ViewUnitBean(
			rs.getString(1),
			rs.getString(2),
			rs.getInt(3),
			rs.getString(4),
			rs.getString(5),
			rs.getString(6)
		);
	}
	
	public static ViewUserBean extractUserViewBean(ResultSet rs)
			throws SQLException, ParseException {
		return new ViewUserBean(
			rs.getString("id"),
			rs.getString("name"),
			rs.getString("social_number"),
			rs.getString("gender"),
			rs.getString("region"),
			rs.getString("dept_name"),
			rs.getString("job_name"),
			rs.getString("postal_code"),
			rs.getString("address1"),
			rs.getString("address2"),
			rs.getInt("salary"),
			rs.getString("extension_phone"),
			rs.getString("mobile_phone"),
			rs.getString("email"),
			WebHelper.getDate(rs.getString("hired_at"))
		);
	}
	
	public static ViewWorkBean extractViewWorkBean(ResultSet rs)
	throws SQLException {
		return new ViewWorkBean(
			rs.getInt("id"),
			rs.getString("user_id"),
			rs.getString("name"),
			rs.getString("dept_name"),
			rs.getString("job_name"),
			rs.getString("started_at"),
			rs.getString("ended_at"),
			rs.getInt("status")
		);
	}
}
