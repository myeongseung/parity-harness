<!-- downloadFile.jsp -->
<%@page import="java.io.BufferedOutputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.File"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="helper.WebHelper"%>
<jsp:useBean id="fileCon" class="controller.PostFileController" scope="page" />
<%
final String SAVE_FOLDER = request.getServletContext().getRealPath("/upload");

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
String filename = request.getParameter("file");

if (filename != null) {
	File downloadFile = new File(SAVE_FOLDER + File.separator + filename);
	byte[] fileBytes = new byte[(int)downloadFile.length()];
	String newFilename = fileCon.getOriginalFileName(filename);
	
	if (newFilename == null) {
		response.sendRedirect("../accessError.jsp");
		return;
	}
	response.setHeader("Accept-Ranges", "bytes");
	String strClient = request.getHeader("User-Agent");
	
	if (strClient.indexOf("Trident") > 0 || strClient.indexOf("MSIE") > 0) {
		response.setContentType("application/smnet;charset=UTF-8");
		response.setHeader("Content-Disposition", "filename="
		+ new String(newFilename.getBytes("EUC-KR"), "8859_1") + ";");
	} else {
		response.setContentType("application/smnet;charset=UTF-8");
		response.setHeader("Content-Disposition",
				"attachment;filename=" 
		+ new String(newFilename.getBytes("UTF-8"), "ISO-8859-1") + ";");
	}
	out.clear();
	
	if (downloadFile.isFile()) {
		try (BufferedInputStream fin =
				new BufferedInputStream(new FileInputStream(downloadFile))) {
			try (BufferedOutputStream fout =
					new BufferedOutputStream(response.getOutputStream())) {
				int read = 0;
				
				while ((read = fin.read(fileBytes)) != -1) {
					fout.write(fileBytes, 0, read);
				}
			}
		}
	}
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
%>