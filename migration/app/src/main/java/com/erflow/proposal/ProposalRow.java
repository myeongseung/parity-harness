package com.erflow.proposal;

/**
 * 결재 리스트 한 줄. {@code proposal_view} 한 행 중 목록이 쓰는 칸.
 *
 * @param id 번호
 * @param documentId 문서번호
 * @param subject 문서제목
 * @param receivedAt 생성날짜
 */
public record ProposalRow(long id, long documentId, String subject, String receivedAt) {
}
