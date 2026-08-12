package com.erflow.message;

import com.erflow.common.Pagination;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 쪽지 업무.
 *
 * <p>레거시는 페이징 계산을 {@code message/index.jsp} 스크립틀릿에, DB 접근을
 * {@code MessageServiceImpl} 에 두었다. 화면에서 로직을 걷어내 여기로 모은다.
 */
@Service
public class MessageService {

    /** 받은쪽지함. 레거시 {@code class=receiver}. */
    public static final String RECEIVER = "receiver";

    /** 보낸쪽지함. 레거시 {@code class=sender}. */
    public static final String SENDER = "sender";

    private final MessageMapper messageMapper;

    /**
     * @param messageMapper 쪽지 매퍼
     */
    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    /**
     * 쪽지함 한 페이지와 페이징 정보.
     *
     * @param className receiver 또는 sender
     * @param classId 현재 사용자 사번
     * @param search 검색 조건
     * @param requestedPage 요청된 페이지
     * @return 목록과 페이징
     */
    @Transactional(readOnly = true)
    public MessagePage list(String className, String classId, MessageSearch search, int requestedPage) {
        int total = messageMapper.countBy(className, classId, search);
        Pagination pagination = Pagination.of(total, requestedPage);
        List<MessageRow> rows = messageMapper.findPage(
                className, classId, search, pagination.start(), pagination.numPerPage());
        return new MessagePage(rows, pagination);
    }

    /**
     * 쪽지를 «읽음» 으로 표시하고 내용을 읽어 온다.
     *
     * <p>레거시 {@code read.jsp} 는 여는 순간 읽음 처리를 했다. <b>화면을 여는 것이
     * 대상을 바꾼다</b> — 조회수(D-029)와 같은 부류다. 두 앱이 같은 스키마를 보면 한 쪽에서
     * 읽는 순간 다른 쪽에도 읽음으로 보인다. 그대로 옮긴다(D-046).
     *
     * @param id 쪽지 번호
     * @return 쪽지. 없으면 {@code null}
     */
    @Transactional
    public MessageDetail read(int id) {
        messageMapper.markRead(id);
        return messageMapper.findView(id);
    }

    /**
     * 쪽지를 여러 사람에게 보낸다.
     *
     * <p>레거시는 받는 사람을 {@code ;} 로 이어 보냈다(사용자 찾기가 여러 명을 그렇게
     * 넘긴다). 한 명씩 넣고 결과를 {@code &=} 로 모은다.
     *
     * @param senderId 보낸 사람 사번
     * @param receiverIds 받는 사람 사번 목록
     * @param content 내용
     * @return 전부 보내졌으면 {@code true}
     */
    @Transactional
    public boolean send(String senderId, List<String> receiverIds, String content) {
        boolean all = true;
        for (String receiverId : receiverIds) {
            all &= messageMapper.insertMessage(senderId, receiverId, content) == 1;
        }
        return all;
    }

    /**
     * 쪽지함 한 페이지.
     *
     * @param rows 이 페이지의 쪽지 목록
     * @param pagination 페이징 정보
     */
    public record MessagePage(List<MessageRow> rows, Pagination pagination) {
    }
}
