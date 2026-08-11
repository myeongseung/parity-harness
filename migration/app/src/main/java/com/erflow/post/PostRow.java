package com.erflow.post;

import java.time.LocalDateTime;

/**
 * 게시글 한 건. 레거시 {@code post_view} 한 행에 대응한다.
 *
 * <h2>{@code depth} 는 트리 깊이가 아니다</h2>
 *
 * <p>이름과 달리 {@code depth} 는 <b>스레드 그룹 번호</b>이고, 들여쓰기 깊이는
 * {@code pos} 다. 레거시 목록 화면이 {@code pos} 만큼 공백을 찍고 {@code pos > 0}
 * 이면 "답글 : " 을 붙이는 것으로 확인했다. 정렬이
 * {@code order by depth desc, pos} 인 것도 이 해석이라야 말이 된다 — 최신 스레드
 * 먼저, 스레드 안에서는 들여쓰기 순서.
 *
 * <p>실데이터에는 {@code pos > 0} 인 글이 한 건도 없다. 답글 표시 경로는 실화면
 * 대조로 검증할 수 없다는 뜻이다.
 *
 * @param id 글번호
 * @param boardId 게시판 번호
 * @param userId 작성자 사번
 * @param name 작성자명
 * @param refId 스레드 뿌리 글번호
 * @param subject 제목
 * @param content 본문
 * @param depth 스레드 그룹 번호
 * @param pos 들여쓰기 깊이
 * @param count 조회수
 * @param createdAt 작성일
 */
public record PostRow(
        int id,
        int boardId,
        String userId,
        String name,
        Integer refId,
        String subject,
        String content,
        int depth,
        int pos,
        int count,
        LocalDateTime createdAt) {

    /**
     * 답글인지 여부.
     *
     * @return 레거시가 "답글 : " 을 붙이던 조건({@code pos > 0})
     */
    public boolean reply() {
        return pos > 0;
    }
}
