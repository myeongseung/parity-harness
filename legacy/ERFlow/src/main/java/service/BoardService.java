package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.BoardBean;

/**
 * @author
 */
public interface BoardService extends Service {
	boolean createBoard(HttpSession session, BoardBean board);

	boolean createBoard(HttpSession session, HttpServletRequest request);

	boolean deleteBoard(HttpSession session, int boardId);
	
	boolean hasBoardName(String name);

	boolean updateBoard(HttpSession session, BoardBean board);

	boolean updateBoard(HttpSession session, HttpServletRequest request);

	int getBoardCount(String keyword);
	
	BoardBean getBoard(int boardId);

	Vector<BoardBean> getBoards();
	
	Vector<BoardBean> getBoards(String keyword);
	
	Vector<BoardBean> getBoards(String keyword, int start, int cnt);
}
