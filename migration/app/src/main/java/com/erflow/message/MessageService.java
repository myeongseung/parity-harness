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
     * 쪽지함 한 페이지.
     *
     * @param rows 이 페이지의 쪽지 목록
     * @param pagination 페이징 정보
     */
    public record MessagePage(List<MessageRow> rows, Pagination pagination) {
    }
}
