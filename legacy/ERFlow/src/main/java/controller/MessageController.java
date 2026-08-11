package controller;

import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import helper.WebHelper;
import model.MessageBean;
import model.UserBean;
import model.view.ViewMessageBean;
import service.implementation.MessageServiceImpl;

public class MessageController {
	private final MessageServiceImpl msgSvc;
	
	public MessageController() {
		msgSvc = new MessageServiceImpl();
	}
	
	public boolean createMessage(HttpSession session, MessageBean bean) {
		UserBean user = WebHelper.getValidUser(session);
		boolean result = false;
		
		if (user != null && bean != null) {
			result = msgSvc.createMessage(user, bean);
		}
		return result;
	}
	
	public boolean createMessage(HttpSession session, HttpServletRequest request) {
		UserBean user = WebHelper.getValidUser(session);
		boolean result = false;
		
		if (user != null) {
			result = msgSvc.createMessage(user, request);
		}
		return result;
	}
	
	public boolean createMessages(HttpSession session, Vector<String> receivers, MessageBean bean) {
		UserBean user = WebHelper.getValidUser(session);
		boolean result = true;
		
		if (user != null && receivers != null && bean != null) {
			for (String receiver : receivers) {
				bean.setReceiverId(receiver);
				result &= msgSvc.createMessage(user.getId(), bean);
			}
		} else {
			result = false;
		}
		return result;
	}
	
	public boolean createMessages(HttpSession session, HttpServletRequest request) {
		UserBean user = WebHelper.getValidUser(session);
		boolean result = true;
		
		if (user != null && request != null) {
			final String[] keys = {
				"senderId", "content"
			};
			String[] receivers = request.getParameterValues("receiverId");
			HashMap<String, String> parameters = new HashMap<>();
			
			if (receivers != null) {
				for (String key : keys) {
					String value = request.getParameter(key);
					
					if (value == null) {
						result = false;
						break;
					}
					parameters.put(key, value);
				}
				if (result) {
					for (String receiver : receivers) {
						MessageBean bean = new MessageBean(
							-1,
							parameters.get("senderId"),
							receiver,
							parameters.get("content"),
							null,
							null,
							0,
							1,
							1
						);
						result &= createMessage(session, bean);
					}
				}
			} else {
				result = false;
			}
		} else {
			result = false;
		}
		return result;
	}
	
	public boolean deleteMessage(HttpSession session, int messageId) {
		UserBean user = WebHelper.getValidUser(session);
		boolean result = false;
		
		if (user != null) {
			result = msgSvc.deleteMessage(user, messageId);
		}
		return result;
	}
	
	@Deprecated
	public boolean deleteMessages(HttpSession session, int[] messageIds) {
		UserBean user = WebHelper.getValidUser(session);
		boolean result = false;
		
		if (user != null) {
			result = msgSvc.deleteMessages(user, messageIds);
		}
		return result;
	}
	
	public void readMessage(HttpSession session, int messageId) {
		MessageBean msg = getMessage(session, messageId);
		
		if (msg != null) {
			UserBean user = WebHelper.getValidUser(session);
			int readStatus = msg.getReadStatus();
			
			if (user != null && readStatus == 0) {
				msgSvc.readMessage(user, messageId);
			}
		}
	}
	
	public MessageBean getMessage(HttpSession session, int messageId) {
		MessageBean msg = null;
		UserBean user = WebHelper.getValidUser(session);
		
		if (user != null) {
			msg = msgSvc.getMessage(messageId);
		}
		return msg;
	}
	
	public ViewMessageBean getMessageView(HttpSession session, int messageId) {
		ViewMessageBean msg = null;
		UserBean user = WebHelper.getValidUser(session);
		
		if (user != null) {
			msg = msgSvc.getMessageView(messageId);
		}
		return msg;
	}
	
	public Vector<MessageBean> getMessages(String keyfield, String keyword, String className, int start, int cnt) {
		return msgSvc.getMessages(keyfield, keyword, start, cnt);
	}
	
	public Vector<ViewMessageBean> getMessageViews(String keyfield, String keyword, String className, String classId, int start, int cnt) {
		return msgSvc.getMessageViews(keyfield, keyword, className, classId, start, cnt);
	}
	
	public int getMessageCount(String keyfield, String keyword, String className, String classId) {
		return msgSvc.getMessageCount(keyfield, keyword, className, classId);
	}
}
