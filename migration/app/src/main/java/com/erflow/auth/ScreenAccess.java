package com.erflow.auth;

/**
 * 화면 하나가 요구하는 권한.
 *
 * <p>{@code screen} 테이블이 경로를 프로그램에 잇고, 프로그램이 요구 비트마스크를 갖는다.
 *
 * @param route 요청 경로
 * @param programName 권한 프로그램명. 화면 라벨과 다를 수 있다
 * @param deptLevel 요구 부서 권한
 * @param jobLevel 요구 직급 권한
 */
public record ScreenAccess(String route, String programName, long deptLevel, long jobLevel) {
}
