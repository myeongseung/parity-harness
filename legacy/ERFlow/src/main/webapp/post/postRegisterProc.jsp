<!-- postRegisterProc.jsp -->
<%@page import="model.UserBean"%>
<%@page import="java.util.UUID"%>
<%@page import="model.FileBean"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.HashMap"%>
<%@page import="helper.WebHelper"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page contentType="text/html; charset=UTF-8"%>
<jsp:useBean id="postCon" class="controller.PostController"/>
<jsp:useBean id="fileCon" class="controller.PostFileController"/>
<%
final String SAVE_FOLDER = request.getServletContext().getRealPath("/upload");

UserBean user = WebHelper.getValidUser(session);

if (!WebHelper.isLogin(session)) {
	response.sendRedirect("../permissionError.jsp");
	return;
}
File dir = new File(SAVE_FOLDER);

if (!dir.exists()) {
	dir.mkdir();
}
DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
fileItemFactory.setRepository(dir);
fileItemFactory.setSizeThreshold(1024 * 1024 * 20);

ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);

final HashMap<String, String> parameters = new HashMap<>();
final Vector<FileBean> files = new Vector<>();
boolean isValid = false;

try {
	List<FileItem> items = fileUpload.parseRequest(request);
	
	for (FileItem item : items) {
		if (item.isFormField()) {
			// 일반 쿼리 문자열
			parameters.put(item.getFieldName(), item.getString("UTF-8"));
		} else {
			File uploadFile = null;
			String filename = item.getName();
			String originalName = WebHelper.extractFileName(filename);
			String uuidName = null;
			String extension = WebHelper.extractFileExtension(filename);
			long size = item.getSize();
			
			if (filename.trim().equals("")) {
				continue;
			}
			do {
				uuidName = UUID.randomUUID().toString();
				uploadFile = new File(SAVE_FOLDER + File.separator + uuidName);
			} while (uploadFile.exists());
			
			item.write(uploadFile);
			
			FileBean bean = new FileBean(
				-1,
				-1,
				originalName,
				uuidName,
				extension,
				size
			);
			files.addElement(bean);
		}
	}
	isValid = true;
} catch (Exception e) {
	
}
String message = "게시물을 등록하지 못했습니다.";
boolean isCreated = true;
String boardId = "";
int postId = -1;

if (isValid) {	
	parameters.put("userId", user.getId());
	
	boardId = parameters.get("boardId");
	postId = postCon.createPost(session, parameters);
	
	for (FileBean bean : files) {
		bean.setRefId(postId);
		isCreated &= fileCon.createFile(bean);
	}
} else {
	response.sendRedirect("../accessError.jsp");
	return;
}
if (postId != -1 && isCreated) {
	message = "게시글이 등록되었습니다.";
}
%>
<body>
	<script type="text/javascript">
		alert('<%=message%>');
		location.href = 'postList.jsp?boardId=<%=boardId%>';
	</script>
</body>