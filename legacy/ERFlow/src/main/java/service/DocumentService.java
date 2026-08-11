package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.DocumentBean;
import model.view.ViewDocumentBean;

public interface DocumentService extends Service {	
	boolean changeStatus(long documentId, int status);
	
	boolean createDocument(DocumentBean bean);
	
	boolean createDocument(HttpServletRequest request);

	boolean deleteDocument(long documentId);
	
	boolean hasProposal(long documentId);
	
	boolean updateDocument(DocumentBean bean);
	
	boolean updateDocument(HttpServletRequest request);
	
	int getDocumentCount(String keyfield, String keyword, String flag, String value);
	
	DocumentBean getDocument(long documentId);
	
	ViewDocumentBean getDocumentView(long documentId);

	Vector<DocumentBean> getDocuments(String keyfield, String keyword, String flag, String value, int start, int cnt);
	
	Vector<ViewDocumentBean> getDocumentViews();
	
	Vector<ViewDocumentBean> getDocumentViews(String keyfield, String keyword, String flag, String value);
	
	Vector<ViewDocumentBean> getDocumentViews(String keyfield, String keyword, String flag, String value, int start, int cnt);
}
