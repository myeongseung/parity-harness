package service.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import helper.ResultSetExtractHelper;
import model.FileBean;
import service.PostFileService;

public class PostFileServiceImpl implements PostFileService {
	private final DBConnectionServiceImpl pool;
	
	public static final String SAVE_FOLDER = "C:\\Users\\dita810\\Desktop\\filetest";
	public static final String ENCODING = "UTF-8";
	public static final int MAX_SIZE = 1024*1024*20;		//20MB
	
	public PostFileServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}
	
	@Deprecated
	@Override
	public boolean createFile(HttpServletRequest request) {
		boolean flag = false;
		Connection con = null;
		PreparedStatement pstmt = null;
		int postId = -1;
		
        try {
        	
        	String userId = request.getParameter("userId");
        	int boardId = Integer.parseInt(request.getParameter("boardId"));
        	String content = request.getParameter("content");
        	String subject = request.getParameter("subject");
        	String fileName = null;
        	String extension = null;
        	int fileSize = 0;
        		
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            List<FileItem> fileItems = upload.parseRequest(request);
            
            // 게시글 정보 삽입
    		con = pool.getConnection();
    		String sql = "insert into post_tbl(user_tbl_id, board_tbl_id, post_tbl_ref_id, subject, content, depth, pos, count, created_at) "
    					+ "values(?, ?, LAST_INSERT_ID(), ?, ?, 0, 0, 0, now())";
    		pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        	pstmt.setString(1, userId);
        	pstmt.setInt(2, boardId);
        	pstmt.setString(3, subject);
        	pstmt.setString(4, content);
        
        	int cnt = pstmt.executeUpdate();
        	
        	if (cnt == 1) { 
        		ResultSet generatedKeys = pstmt.getGeneratedKeys();
        		
        		if (generatedKeys.next()) {
        			postId = generatedKeys.getInt(1); // 해당 게시글의 post_tbl_id 를 받아옴
        		}
        		for (FileItem item : fileItems) {
            		if(!item.isFormField()) { // 파일인경우		
            			fileName = new File(item.getName()).getName();
            			extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            			fileSize = (int) item.getSize();
                    
            			String filePath = SAVE_FOLDER + File.separator + fileName;

            			File uploadedFile = new File(filePath);
            			FileInputStream fileInputStream = new FileInputStream(uploadedFile);
            			// 파일을 서버에 저장
            			item.write(uploadedFile);
            			// 데이터베이스에 파일 정보 삽입
            			String filesql = "insert into post_file_tbl(post_tbl_id, original_name, name, extension, size) "
                						+ "values(?, ?, ?, ?)";
            			pstmt = con.prepareStatement(filesql);
            			
            			pstmt.setInt(1, postId);
            			pstmt.setString(2, fileName);
            			pstmt.setString(3, fileName);
            			pstmt.setString(4, extension);
            			pstmt.setInt(5, fileSize);
                              
            			int postCnt = pstmt.executeUpdate();
            			if (postCnt > 0) {
            				flag = true; // 게시글 삽입 성공
                		}
                	}
            	} 
        	}
        }
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Deprecated
	@Override
	public boolean createFile(Vector<FileBean> files, int parentPostId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean createFile(FileBean file) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "insert into post_file_tbl values (null, ?, ?, ?, ?, ?)";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, file.getRefId());
			pstmt.setString(2, file.getOriginalName());
			pstmt.setString(3, file.getName());
			pstmt.setString(4, file.getExtension());
			pstmt.setLong(5, file.getSize());
			
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean deleteFiles(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			String sql = "delete from post_file_tbl where post_tbl_id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, postId);
			
			flag = pstmt.executeUpdate() >= 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean updateFile(HttpServletRequest reqeust) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String getOriginalFileName(String encoded) {
		Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String result = null;

	    try {
	        con = pool.getConnection();
	        String sql = "select original_name, extension from post_file_tbl "
	        		+ "where name = ?";
	        pstmt = con.prepareStatement(sql);
	        
	        pstmt.setString(1, encoded);
	        
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	        	String name = rs.getString("original_name");
	        	String extension = rs.getString("extension");
	        	result = name + "." + extension;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        pool.freeConnection(con, pstmt, rs);
	    }
	    return result;
	}
	
	@Override
	public FileBean getFileByPostId(int postId) {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    FileBean fileBean = null;

	    try {
	        con = pool.getConnection();
	        String sql = "select * from post_file_tbl where post_tbl_id = ?";
	        pstmt = con.prepareStatement(sql);
	        pstmt.setInt(1, postId);
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	            fileBean = new FileBean(
	                rs.getInt("id"),
	                rs.getInt("post_tbl_id"),
	                rs.getString("original_name"),
	                rs.getString("name"),
	                rs.getString("extension"),
	                rs.getLong("size")
	            );
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        pool.freeConnection(con, pstmt, rs);
	    }
	    return fileBean;
	}
	
	@Override
	public Vector<FileBean> getFiles(int postId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<FileBean> vlist = new Vector<>();

		try {
			String sql = "select * from post_file_tbl where post_tbl_id = ? "
					+ "order by original_name, extension, size, id";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, postId);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractPostFileBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}
}
