package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.BoardBean;
import model.CommentBean;
import model.DepartmentBean;
import model.DocumentBean;
import model.JobBean;
import model.PostBean;
import model.TemplateBean;
import model.UnitBean;
import model.view.ViewCommentBean;
import model.view.ViewDocumentBean;
import model.view.ViewPostBean;
import model.view.ViewProposalBean;
import model.view.ViewUnitBean;
import model.view.ViewUserBean;
import model.view.ViewWorkBean;

public class ActivityController {
	private final BoardController boardCon;
	private final CommentController commentCon;
	private final DepartmentController deptCon;
	private final DocumentController documentCon;
	private final JobController jobCon;
	private final PostController postCon;
	private final ProposalController proposalCon;
	private final TemplateController templateCon;
	private final UnitController unitCon;
	private final UserController userCon;
	private final WorkController workCon;

	public ActivityController() {
		boardCon = new BoardController();
		commentCon = new CommentController();
		deptCon = new DepartmentController();
		documentCon = new DocumentController();
		jobCon = new JobController();
		postCon = new PostController();
		proposalCon = new ProposalController();
		templateCon = new TemplateController();
		unitCon = new UnitController();
		userCon = new UserController();
		workCon = new WorkController();
	}

	// BoardController
	public int getBoardCount(String keyword) {
		return boardCon.getBoardCount(keyword);
	}
	
	public BoardBean getBoard(int boardId) {
		return boardCon.getBoard(boardId);
	}
	
	public Vector<BoardBean> getBoards(String keyword) {
		return boardCon.getBoards(keyword);
	}
	
	// CommentController
	public boolean createComment(HttpSession session, CommentBean comment) {
		return commentCon.createComment(session, comment);
	}

	public boolean createComment(HttpSession session, HttpServletRequest request) {
		return commentCon.createComment(session, request);
	}

	public boolean deleteComment(HttpSession session, int commentId) {
		return commentCon.deleteComment(session, commentId);
	}

	public boolean updateComment(HttpSession session, CommentBean comment) {
		return commentCon.updateComment(session, comment);
	}

	public CommentBean getComment(int commentId) {
		return commentCon.getComment(commentId);
	}

	public Vector<CommentBean> getComments(int postId) {
		return commentCon.getComments(postId);
	}

	public Vector<ViewCommentBean> getCommentViews(int postId) {
		return commentCon.getCommentViews(postId);
	}

	public boolean createReply(HttpSession session, CommentBean comment) {
		return commentCon.createReply(session, comment);
	}

	// UnitController
	public boolean createUnit(HttpSession session, HttpServletRequest request) {
		return unitCon.createUnit(session, request);
	}

	public boolean createUnit(HttpSession session, UnitBean bean) {
		return unitCon.createUnit(session, bean);
	}

	public boolean deleteUnit(HttpSession session, String unitId) {
		return unitCon.deleteUnit(session, unitId);
	}

	public boolean updateUnit(HttpSession session, HttpServletRequest request) {
		return unitCon.updateUnit(session, request);
	}

	public Vector<ViewUnitBean> getUnits(String keyfield, String keyword, int start, int cnt) {
		return unitCon.getUnits(keyfield, keyword, start, cnt);
	}

	public int getUnitCount(String keyfield, String keyword) {
		return unitCon.getUnitCount(keyfield, keyword);
	}

	// DepartmentController

	public Vector<DepartmentBean> getDepartments(String keyword) {
		return deptCon.getDepts(keyword);
	}

	// DocumentController
	public boolean changeStatus(HttpSession session, long documentId, int status) {
		return documentCon.changeStatus(session, documentId, status);
	}

	public boolean createDocument(HttpSession session, DocumentBean bean) {
		return documentCon.createDocument(session, bean);
	}

	public boolean createDocument(HttpSession session, HttpServletRequest request) {
		return documentCon.createDocument(session, request);
	}

	public boolean deleteDocument(HttpSession session, long documentId) {
		return documentCon.deleteDocument(session, documentId);
	}
	
	public boolean hasProposal(long documentId) {
		return documentCon.hasProposal(documentId);
	}

	public boolean updateDocument(HttpSession session, HttpServletRequest request) {
		return documentCon.updateDocument(session, request);
	}

	public boolean updateDocument(HttpSession session, DocumentBean bean) {
		return documentCon.updateDocument(session, bean);
	}

	public int getDocumentCount(String keyfield, String keyword, String flag, String value) {
		return documentCon.getDocumentCount(keyfield, keyword, flag, value);
	}

	public ViewDocumentBean getDocumentView(long documentId) {
		return documentCon.getDocumentView(documentId);
	}

	public Vector<ViewDocumentBean> getDocumentViews() {
		return documentCon.getDocumentViews();
	}

	public Vector<ViewDocumentBean> getDocumentViews(String keyfield, String keyword, String flag, String value) {
		return documentCon.getDocumentViews(keyfield, keyword, flag, value);
	}

	public Vector<ViewDocumentBean> getDocumentViews(String keyfield, String keyword, String flag, String value,
			int start, int cnt) {
		return documentCon.getDocumentViews(keyfield, keyword, flag, value, start, cnt);
	}

	// JobController

	public Vector<JobBean> getJobs(String keyword) {
		return jobCon.getJobs(keyword);
	}

	// PostController
	
	public boolean deletePost(HttpSession session, int boardId, int postId) {
		return postCon.deletePost(session, boardId, postId);
	}

	public boolean updateCount(int postId) {
		return postCon.updateCount(postId);
	}

	public int getTotalCount(int boardId, String keyfield, String keyword) {
		return postCon.getTotalCount(boardId, keyfield, keyword);
	}

	public int getTotalCommentCount(int postId) {
		return postCon.getTotalCommentCount(postId);
	}

	public ViewPostBean getPostView(HttpSession session, int boardId, int postId) {
		return postCon.getPostView(session, boardId, postId);
	}

	public Vector<PostBean> getPosts(int boardId, String keyfield, String keyword, int start, int cnt) {
		return postCon.getPosts(boardId, keyfield, keyword, start, cnt);
	}

	public Vector<ViewPostBean> getPostViews(int boardId, String keyfield, String keyword, int start, int cnt) {
		return postCon.getPostViews(boardId, keyfield, keyword, start, cnt);
	}
	
	// ProposalController
	
	public Vector<ViewProposalBean> getRecentProposalViews(String userId, int start, int cnt) {
		return proposalCon.getRecentProposalViews(userId, start, cnt);
	}
	
	
	public Vector<ViewProposalBean> getProposalViews(String userId, int result, int start, int cnt) {
		return proposalCon.getProposalViews(userId, result, start, cnt);
	}
	
	// TemplateController

	public Vector<TemplateBean> getTemplates() {
		return templateCon.getTemplates();
	}

	// UserController
	
	public int getUserTotalCount(String keyfield1, String keyfield2, String keyword) {
		return userCon.getUserTotalCount(keyfield1, keyfield2, keyword);
	}
	
	public ViewUserBean getUserView(String userId) {
		return userCon.getUserView(userId);
	}

	public Vector<ViewUserBean> getUserViews(String keyfield1, String keyfield2, String keyword) {
		return userCon.getUserViews(keyfield1, keyfield2, keyword);
	}

	public Vector<ViewUserBean> getUserViews(String keyfield1, String keyfield2, String keyword, int start, int cnt) {
		return userCon.getUserViews(keyfield1, keyfield2, keyword, start, cnt);
	}
	
	
	// WorkController
	
	public Vector<ViewWorkBean> getWorkViews(String date) {
		return workCon.getWorkViews(date);
	}
	
	public Vector<ViewWorkBean> getWorkViews(String id, String date) {
		return workCon.getWorkViews(id, date);
	}
}
