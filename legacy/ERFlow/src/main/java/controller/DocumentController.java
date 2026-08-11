package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.WebHelper;
import model.DocumentBean;
import model.view.ViewDocumentBean;
import service.implementation.DocumentServiceImpl;

public class DocumentController {
	private final DocumentServiceImpl docSvc;
	
	public DocumentController() {
		docSvc = new DocumentServiceImpl();
	}
	
	public boolean changeStatus(HttpSession session, long documentId, int status) {
		boolean result = false;
		
		if (WebHelper.isLogin(session)) {
			result = docSvc.changeStatus(documentId, status);
		}
		return result;
	}
	
	public boolean createDocument(HttpSession session, DocumentBean bean) {
		boolean result = false;
		
		if (WebHelper.isLogin(session)) {
			result = docSvc.createDocument(bean);
		}
		return result;
	}
	
	public boolean createDocument(HttpSession session, HttpServletRequest request) {
		boolean result = false;
		
		if (WebHelper.isLogin(session)) {
			result = docSvc.createDocument(request);
		}
		return result;
	}
	
	public boolean deleteDocument(HttpSession session, long documentId) {
		boolean result = false;
		DocumentBean bean = docSvc.getDocument(documentId);
		
		// 사용자의 로그인 상태를 확인하고, 삭제할 권한이 있는 사용자인지 확인
		if (WebHelper.isLogin(session) && bean != null &&
				WebHelper.isAuthorizedUser(session, bean.getUserId())) {
			result = docSvc.deleteDocument(documentId);
		}
		return result;
	}
	
	public boolean hasProposal(long documentId) {
		return docSvc.hasProposal(documentId);
	}
	
	public boolean updateDocument(HttpSession session, HttpServletRequest request) {
		boolean result = false;
		DocumentBean bean = null;
		
		if (request != null) {
			long documentId = -1L;
			
			if (request.getParameter("id") != null) {
				documentId = WebHelper.parseLong(request, "id");
			}
			bean = docSvc.getDocument(documentId);
		}
		
		// 사용자의 로그인 상태를 확인하고, 삭제할 권한이 있는 사용자인지 확인
		if (WebHelper.isLogin(session) && bean != null &&
				WebHelper.isAuthorizedUser(session, bean.getUserId())) {
			result = docSvc.updateDocument(request);
		}
		return result;
	}
	
	public boolean updateDocument(HttpSession session, DocumentBean bean) {
		boolean result = false;
		
		// 사용자의 로그인 상태를 확인하고, 삭제할 권한이 있는 사용자인지 확인
		if (WebHelper.isLogin(session) && bean != null &&
				WebHelper.isAuthorizedUser(session, bean.getUserId())) {
			result = docSvc.updateDocument(bean);
		}
		return result;
	}
	
	public int getDocumentCount(String keyfield, String keyword, String flag, String value) {
		return docSvc.getDocumentCount(keyfield, keyword, flag, value);
	}
	
	public ViewDocumentBean getDocumentView(long documentId) {
		return docSvc.getDocumentView(documentId);
	}
	
	public Vector<ViewDocumentBean> getDocumentViews() {
		return docSvc.getDocumentViews();
	}
	
	public Vector<ViewDocumentBean> getDocumentViews(String keyfield, String keyword, String flag, String value) {
		return docSvc.getDocumentViews(keyfield, keyword, flag, value);
	}
	
	public Vector<ViewDocumentBean> getDocumentViews(String keyfield, String keyword, String flag, String value, int start, int cnt) {
		return docSvc.getDocumentViews(keyfield, keyword, flag, value, start, cnt);
	}
}
