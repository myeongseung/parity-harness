package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.MessageBean;
import model.UserBean;
import model.view.ViewMessageBean;

public interface MessageService extends Service {
	// 쪽지 생성 및 전송
	boolean createMessage(String userId, MessageBean bean);
	boolean createMessage(UserBean user, MessageBean bean);
	boolean createMessage(String userId, HttpServletRequest request);
	boolean createMessage(UserBean user, HttpServletRequest request);
	// 쪽지 삭제
	boolean deleteMessage(String userId, int messageId);
	boolean deleteMessage(UserBean user, int messageId);
	boolean deleteMessages(String userId, int[] messageIds);
	boolean deleteMessages(UserBean user, int[] messageIds);
	boolean deleteMessages(String userId, Vector<Integer> messageIds);
	boolean deleteMessages(UserBean user, Vector<Integer> messageIds);
	// 쪽지 하나 불러오기
	MessageBean getMessage(int messageId);
	ViewMessageBean getMessageView(int messageId);
	// 쪽지 전체 불러오기
	Vector<MessageBean> getMessages(String keyfield, String keyword, int start, int cnt);
	Vector<ViewMessageBean> getMessageViews(String keyfield, String keyword, String className, String classId, int start, int cnt);
	
	// 쪽지 읽음 처리
	void readMessage(String userId, int messageId);
	void readMessage(UserBean user, int messageId);
	
	int getMessageCount(String keyfield, String keyword, String className, String classId);
}
