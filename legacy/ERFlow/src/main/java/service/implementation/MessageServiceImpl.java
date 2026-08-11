package service.implementation;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import helper.ResultSetExtractHelper;
import model.MessageBean;
import model.UserBean;
import model.view.ViewMessageBean;
import service.MessageService;

public class MessageServiceImpl implements MessageService {
	private final DBConnectionServiceImpl pool;

	public MessageServiceImpl() {
		pool = DBConnectionServiceImpl.getInstance();
	}

	@Override
	public boolean createMessage(String userId, MessageBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;
		
		// 보내는 사람이 현재 로그인된 사용자랑 같아야 보낼 수 있음.
		if (userId != null && bean != null &&
				userId.equals(bean.getSenderId())) {
			try {
				String sql = "insert into message_tbl (user_tbl_sender_id, "
						+ "user_tbl_receiver_id, content, created_at) "
						+ "values (?, ?, ?, now())";
				con = pool.getConnection();
				pstmt = con.prepareStatement(sql);
				
				pstmt.setString(1, bean.getSenderId());
				pstmt.setString(2, bean.getReceiverId());
				pstmt.setString(3, bean.getContent());
				
				flag = pstmt.executeUpdate() == 1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				pool.freeConnection(con, pstmt);
			}
		}
		return flag;
	}

	@Override
	public boolean createMessage(UserBean user, MessageBean bean) {
		boolean result = false;
		
		// bean의 null 검사는 아래 함수에서 수행하므로 생략한다.
		if (user != null) {
			result = createMessage(user.getId(), bean);
		}
		return result;
	}

	public boolean createMessage(String userId, HttpServletRequest request) {
		final String[] keys = { "senderId", "receiverId", "content" };
		HashMap<String, String> parameters = new HashMap<>();
		boolean result = false;
		
		if (userId != null && request != null) {
			boolean hasNull = false;
			
			// HttpServletRequest Parameters 인자 검사
			for (String key : keys) {
				String value = request.getParameter(key);
				
				if (value == null) {
					hasNull = true;
					break;
				}
				parameters.put(key, value);
			}
			// 쪽지 생성 시도
			if (!hasNull) {
				MessageBean bean = new MessageBean(
					-1,
					parameters.get("senderId"),
					parameters.get("receiverId"),
					parameters.get("content"),
					null,
					null,
					0,
					1,
					1
				);
				result = createMessage(userId, bean);
			}
		}
		return result;
	}

	@Override
	public boolean createMessage(UserBean user, HttpServletRequest request) {
		boolean result = false;
		
		if (user != null) {
			result = createMessage(user.getId(), request);
		}
		return result;
	}

	@Override
	public boolean deleteMessage(UserBean user, int messageId) {
		boolean result = false;
		
		if (user != null) {
			result = deleteMessage(user.getId(), messageId);
		}
		return result;
	}

	public boolean deleteMessage(String userId, int messageId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		if (userId != null) {
			MessageBean bean = getMessage(messageId);
			
			if (bean != null) {
				try {
					boolean isSender = userId.equals(bean.getSenderId());
					boolean isReceiver = userId.equals(bean.getReceiverId());
					StringBuilder sb = new StringBuilder();
					
					if (isSender) {
						sb.append("sender_visible = 0 ");
					}
					if (isReceiver) {
						if (sb.length() > 0) {
							sb.append(", ");
						}
						sb.append("receiver_visible = 0 ");
					}
					String sql = "update message_tbl set " + sb.toString() + "where id = ?";
					con = pool.getConnection();
					pstmt = con.prepareStatement(sql);
					
					pstmt.setInt(1, messageId);
					
					flag = pstmt.executeUpdate() == 1;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pool.freeConnection(con, pstmt);
				}
			}
		}
		return flag;
	}

	@Deprecated
	@Override
	public boolean deleteMessages(UserBean user, int[] messageIds) {
		boolean result = false;
		
		if (user != null) {
			result = deleteMessages(user.getId(), messageIds);
		}
		return result;
	}
	
	@Deprecated
	@Override
	public boolean deleteMessages(String userId, int[] messageIds) {
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean flag = false;

		try {
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < messageIds.length; ++i) {
				sb.append(",?");
			}
			String query = sb.toString().substring(1);
			String sql = "delete from message_tbl where id in (" + query + ")";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			for (int i = 0; i < messageIds.length; ++i) {
				pstmt.setInt(i + 1, messageIds[i]);
			}
			flag = pstmt.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	@Override
	public boolean deleteMessages(UserBean user, Vector<Integer> messageIds) {
		boolean result = false;
		
		if (user != null) {
			result = deleteMessages(user.getId(), messageIds);
		}
		return result;
	}

	@Override
	public boolean deleteMessages(String userId, Vector<Integer> messageIds) {
		boolean result = false;
		
		if (userId != null && messageIds != null) {
			int[] ids = Arrays.stream(messageIds.toArray(new Integer[0]))
					.mapToInt(Integer::intValue)
					.toArray();
			result = deleteMessages(userId, ids);
		}
		return result;
	}

	@Override
	public MessageBean getMessage(int messageId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MessageBean bean = null;

		try {
			String sql = "select * from message_tbl where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, messageId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean = ResultSetExtractHelper
						.extractMessageBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	@Override
	public ViewMessageBean getMessageView(int messageId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ViewMessageBean bean = null;

		try {
			String sql = "select * from message_view where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, messageId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				bean = ResultSetExtractHelper
						.extractViewMessageBean(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	@Override
	public Vector<MessageBean> getMessages(String keyfield, String keyword, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<MessageBean> vlist = new Vector<>();
		boolean flag = false;

		try {
			String additional = "";
			
			if (keyfield != null && !keyfield.trim().equals("")) {
				switch (keyfield) {
					case "receiver" -> {
						additional = " where receiver_name like ?";
						flag = true;
					}
					case "sender" -> {
						additional = " where sender_name like ?";
						flag = true;
					}
					case "content" -> {
						additional = " where content like ?";
						flag = true;
					}
				}
			}
			String sql = "select * from message_tbl" + additional + " limit ?, ?";
			int index = 1;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			if (flag) {
				pstmt.setString(index++, keyword);
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractMessageBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}

	@Override
	public Vector<ViewMessageBean> getMessageViews(String keyfield, String keyword, String className, String classId, int start, int cnt) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<ViewMessageBean> vlist = new Vector<>();
		boolean flag = false;

		try {
			String additional = "";
			
			if (keyfield != null && !keyfield.trim().equals("") &&
					keyword != null && !keyword.trim().equals("")) {
				switch (keyfield) {
					case "receiver" -> {
						additional = " and receiver_name like ?";
						flag = true;
					}
					case "sender" -> {
						additional = " and sender_name like ?";
						flag = true;
					}
					case "content" -> {
						additional = " and content like ?";
						flag = true;
					}
				}
			}
			String sql = "select * from message_view where " + className + "_id = ? and "
			+ className + "_visible = 1" + additional + " order by id desc limit ?, ?";
			int index = 1;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(index++, classId);
			
			if (flag) {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			pstmt.setInt(index++, start);
			pstmt.setInt(index++, cnt);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				vlist.addElement(ResultSetExtractHelper
						.extractViewMessageBean(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}

	@Override
	public void readMessage(UserBean user, int messageId) {
		if (user != null) {
			readMessage(user.getId(), messageId);
		}
	}

	@Override
	public void readMessage(String userId, int messageId) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			String sql = "update message_tbl set read_at = now(), "
					+ "read_status = 1 where id = ?";
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, messageId);

			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
	}
	
	@Override
	public int getMessageCount(String keyfield, String keyword, String className, String classId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			String additional = "";
			boolean flag = false;
			
			if (keyfield != null && !keyfield.trim().equals("") &&
					keyword != null && !keyword.trim().equals("")) {
				switch (keyfield) {
					case "receiver" -> {
						additional = " and receiver_name like ?";
						flag = true;
					}
					case "sender" -> {
						additional = " and sender_name like ?";
						flag = true;
					}
					case "content" -> {
						additional = " and content like ?";
						flag = true;
					}
				}
			}
			String sql = "select count(*) from message_view where " + className + "_id = ? and "
			+ className + "_visible = 1" + additional + " order by id desc";
			int index = 1;
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setString(index++, classId);
			
			if (flag) {
				pstmt.setString(index++, '%' + keyword + '%');
			}
			rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return count;
	}
}
