package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.BoardBean;
import service.implementation.BoardServiceImpl;

public class BoardController {
	private final BoardServiceImpl boardSvc;
		
	public BoardController() {
		boardSvc = new BoardServiceImpl();
		
	}
	
	public boolean createBoard(HttpSession session, HttpServletRequest request) {
		return boardSvc.createBoard(session, request);
	}
	
	public boolean deleteBoard(HttpSession session, int boardId) {
		return boardSvc.deleteBoard(session, boardId);
	}
	
	public boolean hasBoardName(String name) {
		return boardSvc.hasBoardName(name);
	}
	
	public boolean updateBoard(HttpSession session, HttpServletRequest request) {
		return boardSvc.updateBoard(session, request);
	}
	
	public int getBoardCount(String keyword) {
		return boardSvc.getBoardCount(keyword);
	}
	
	public BoardBean getBoard(int boardId) {
		return boardSvc.getBoard(boardId);
	}
	
	public Vector<BoardBean> getBoards() {
		return boardSvc.getBoards();
	}
	
	public Vector<BoardBean> getBoards(String keyword) {
		return boardSvc.getBoards(keyword);
	}
	
	public Vector<BoardBean> getBoards(String keyword, int start, int cnt) {
		return boardSvc.getBoards(keyword, start, cnt);
	}
}
