package com.erflow.admin.user;

/**
 * 사원을 넣거나 고칠 때 서버로 넘어오는 값.
 *
 * <p>레거시 {@code *Proc.jsp} 가 {@code UserBean} 을 채우던 자리에 해당한다. <b>화면이
 * 보낸 값 중 일부가 여기 오지 못한다</b> — 레거시 처리가 그 값을 읽지 않기 때문이다.
 * 등록에서는 주소 두 줄과 휴대 전화가, 수정에서는 휴대 전화가 언제나 {@code null} 이다.
 * 결함이지만 그대로 옮겼다(D-057).
 *
 * @param id 사번
 * @param name 이름
 * @param socialNumber 주민등록번호. 수정에서는 쓰지 않는다
 * @param email 이메일
 * @param postalCode 우편번호
 * @param address1 도로명 주소. 등록에서는 언제나 {@code null}
 * @param address2 상세 주소. 등록에서는 언제나 {@code null}
 * @param jobId 직급 번호
 * @param deptId 부서 번호
 * @param extensionPhone 내선 번호
 * @param mobilePhone 휴대 전화. 등록·수정 모두 언제나 {@code null}
 */
public record AdminUserEdit(
        String id,
        String name,
        String socialNumber,
        String email,
        String postalCode,
        String address1,
        String address2,
        int jobId,
        int deptId,
        String extensionPhone,
        String mobilePhone) {
}
