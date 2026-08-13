-- 생성물. 직접 고치지 말고 migration/tools/build_menu_seed.py 를 고쳐 재생성한다.
-- 세 테이블 모두 생성물이므로 통째로 다시 만든다. FK 때문에 삭제 순서가 있다.
DROP TABLE IF EXISTS `menu`;
DROP TABLE IF EXISTS `screen`;
DROP TABLE IF EXISTS `program`;

-- 레거시는 utf8mb4_0900_ai_ci(MySQL 8) 였다. MariaDB 에 없는 collation 이라
-- utf8mb4_general_ci 로 옮긴다. 한글 정렬 순서가 달라질 수 있어 별도 확인 대상이다.

-- 권한 값(dept_level/job_level)은 여기 두지 않는다. 그 값은 이관 시점의
-- 스냅샷이 아니라 관리자가 화면에서 바꾸는 운영 데이터이고, 이 표는 통째로
-- 다시 만들어지는 생성물이다. 판정은 permission_program_tbl 을 읽는다(D-063).
CREATE TABLE IF NOT EXISTS `program` (
  `program_id` VARCHAR(128)  NOT NULL COMMENT '레거시 SHA256 값 보존. 재생성 금지',
  `name`       VARCHAR(255)  NOT NULL COMMENT '권한 프로그램명. 메뉴 라벨과 다를 수 있다',
  PRIMARY KEY (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `screen` (
  `screen_id`  INT           NOT NULL,
  `route`      VARCHAR(512)  NOT NULL COMMENT '권한 매칭 대상 경로. 쿼리 제외',
  `param_name`  VARCHAR(64)  NULL COMMENT '같은 경로가 파라미터로 권한이 갈릴 때',
  `param_value` VARCHAR(64)  NULL COMMENT '그 파라미터의 값',
  `legacy_jsp` VARCHAR(512)  NOT NULL COMMENT '출처. 추적용',
  `program_id` VARCHAR(128)  NOT NULL,
  PRIMARY KEY (`screen_id`),
  UNIQUE KEY `ux_screen_route` (`route`, `param_name`, `param_value`),
  CONSTRAINT `fk_screen_program` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `menu` (
  `menu_id`    INT           NOT NULL,
  `placement`  VARCHAR(16)   NOT NULL COMMENT 'SIDE | HEADER',
  `parent_id`  INT           NULL COMMENT 'NULL 이면 최상위',
  `sort_order` INT           NOT NULL COMMENT '같은 부모 안에서의 표시 순서',
  `label`      VARCHAR(255)  NOT NULL COMMENT '화면에 보이는 문구. 레거시 원문 유지',
  `visibility` VARCHAR(16)   NOT NULL DEFAULT 'ALWAYS' COMMENT 'ALWAYS | ADMIN',
  `icon`       VARCHAR(64)   NULL COMMENT '아이콘 class. 글자가 없어 놓치기 쉬운 표시 요소다',
  `separator_before` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '이 항목 앞에 구분선(<hr>)',
  `url`        VARCHAR(512)  NULL COMMENT '클릭 시 이동할 주소. 쿼리 포함',
  `screen_id`  INT           NULL COMMENT '가리키는 화면. NULL 이면 그룹이거나 권한 대상 아님',
  PRIMARY KEY (`menu_id`),
  KEY `ix_menu_render` (`placement`, `parent_id`, `sort_order`),
  CONSTRAINT `fk_menu_parent` FOREIGN KEY (`parent_id`) REFERENCES `menu` (`menu_id`),
  CONSTRAINT `fk_menu_screen` FOREIGN KEY (`screen_id`) REFERENCES `screen` (`screen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `program` (`program_id`, `name`) VALUES
  ('5C5D1BE8247DEADA5D073A263A19B4E02240BB7A6D2E8087723AEA0783B68C0C', '가공품 관리'),
  ('4D0B4292FC1A94E622F319AEFB34E9AD4270E32A898591F6DE299357A711C7E4', '게시판 목록 조회'),
  ('53A5157A4BAB5F1B75921C9B42888D7E1539466CD3DD258C1E318F29B62EC586', '결재 리스트'),
  ('C6409ACB532D53B2C7F4065039A8E7FED426810370BCD39330526F662A115A72', '결재관리 관리'),
  ('F03BEAC83BFF5F1D13D55F26C10040BED42E0C5BBAC8EC850574D03C13AE8CFF', '공정 관리'),
  ('4F6B21882D7C310CC96B01D478A381D0DCF410B574B7185E6EA449173834A561', '구매 협력업체 관리'),
  ('B02E912CD7BFFC1760E1C5C009F97386770E5A280C11425C4F94A7B2D11554EA', '근태 관리'),
  ('F8DC1E7F6122BB87B532BB313D9B51294686A38717B78F97A90DE7FCB53D0F6C', '문서 관리'),
  ('4BB57A61E4CE88D4416CD611F74308C927913669E7FE78AE4106D01A9ABAB75A', '문서 작성'),
  ('3879BC2C91F0F2F88E7468299B546E2F2098B09186750212D2247F73306B0A79', '발주 관리'),
  ('822D0CAB0FFF374AAE6D095F0DF8612A3D2C14AB72B4AC1774D5DFD5FB5D29D0', '부서 일정 등록'),
  ('8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2', '생산 설비 관리'),
  ('5D2B70044459C29621CC45B55113F427F933BDE9D43404D387ED8585EFE64AD9', '수주 관리'),
  ('90739BBD5C68921E8D32D44EAB220654A1C51710FDE97F6639DD0CFCBB91E2DB', '영업 협력업체 관리'),
  ('BC58BCABC3239263B4D3838A0673BA319C845BEAD96EFFEF412C3349960FE0B2', '원재료 관리'),
  ('8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0', '입고 관리'),
  ('919BD592CBDEE0FFEF28AC010022C19445DF326E5C26C708C5EC0DA7D9985B02', '쪽지'),
  ('E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7', '출고 관리'),
  ('FD9B4A29561DE0669FF283D9ABE83004605B28ECBC9562A238F0E0B72994FFC1', '출고제품 관리'),
  ('4A7849864C3B8D4067BC853C67BCFA572B7DFE68BBC773372BC14393B1F6C5B8', '프로필');

INSERT INTO `screen` (`screen_id`, `route`, `param_name`, `param_value`, `legacy_jsp`, `program_id`) VALUES
  (1, '/bound/delete-proc', 'flag', 'inbound', '/ERFlow/bound/boundDeleteProc.jsp', '8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0'),
  (2, '/bound/delete-proc', 'flag', 'outbound', '/ERFlow/bound/boundDeleteProc.jsp', 'E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7'),
  (3, '/bound/register', 'flag', 'inbound', '/ERFlow/bound/boundRegister.jsp', '8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0'),
  (4, '/bound/register', 'flag', 'outbound', '/ERFlow/bound/boundRegister.jsp', 'E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7'),
  (5, '/bound/register-proc', 'flag', 'inbound', '/ERFlow/bound/boundRegisterProc.jsp', '8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0'),
  (6, '/bound/register-proc', 'flag', 'outbound', '/ERFlow/bound/boundRegisterProc.jsp', 'E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7'),
  (7, '/bound/update', 'flag', 'inbound', '/ERFlow/bound/boundUpdate.jsp', '8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0'),
  (8, '/bound/update', 'flag', 'outbound', '/ERFlow/bound/boundUpdate.jsp', 'E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7'),
  (9, '/bound/update-proc', 'flag', 'inbound', '/ERFlow/bound/boundUpdateProc.jsp', '8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0'),
  (10, '/bound/update-proc', 'flag', 'outbound', '/ERFlow/bound/boundUpdateProc.jsp', 'E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7'),
  (11, '/bound/inbound', NULL, NULL, '/ERFlow/bound/inbound.jsp', '8DBCEB3F40183429BFB2367E09CC7062C9A2B6C3FAEFACB93796DE3B916D60A0'),
  (12, '/bound/outbound', NULL, NULL, '/ERFlow/bound/outbound.jsp', 'E36738BC1F02168BCAFEE6EA48494ADBC077E402ECD1BAE980DEC1FB5E8144B7'),
  (13, '/company/list', 'flag', '1', '/ERFlow/company/companyList.jsp', '4F6B21882D7C310CC96B01D478A381D0DCF410B574B7185E6EA449173834A561'),
  (14, '/company/list', 'flag', '0', '/ERFlow/company/companyList.jsp', '90739BBD5C68921E8D32D44EAB220654A1C51710FDE97F6639DD0CFCBB91E2DB'),
  (15, '/company/register', 'flag', '1', '/ERFlow/company/companyRegister.jsp', '4F6B21882D7C310CC96B01D478A381D0DCF410B574B7185E6EA449173834A561'),
  (16, '/company/register', 'flag', '0', '/ERFlow/company/companyRegister.jsp', '90739BBD5C68921E8D32D44EAB220654A1C51710FDE97F6639DD0CFCBB91E2DB'),
  (17, '/company/update', 'flag', '1', '/ERFlow/company/companyUpdate.jsp', '4F6B21882D7C310CC96B01D478A381D0DCF410B574B7185E6EA449173834A561'),
  (18, '/company/update', 'flag', '0', '/ERFlow/company/companyUpdate.jsp', '90739BBD5C68921E8D32D44EAB220654A1C51710FDE97F6639DD0CFCBB91E2DB'),
  (19, '/document/delete-proc', NULL, NULL, '/ERFlow/document/documentDeleteProc.jsp', '4BB57A61E4CE88D4416CD611F74308C927913669E7FE78AE4106D01A9ABAB75A'),
  (20, '/document/list', NULL, NULL, '/ERFlow/document/documentList.jsp', 'F8DC1E7F6122BB87B532BB313D9B51294686A38717B78F97A90DE7FCB53D0F6C'),
  (21, '/document/register', NULL, NULL, '/ERFlow/document/documentRegister.jsp', '4BB57A61E4CE88D4416CD611F74308C927913669E7FE78AE4106D01A9ABAB75A'),
  (22, '/document/register-proc', NULL, NULL, '/ERFlow/document/documentRegisterProc.jsp', '4BB57A61E4CE88D4416CD611F74308C927913669E7FE78AE4106D01A9ABAB75A'),
  (23, '/message', NULL, NULL, '/ERFlow/message/index.jsp', '919BD592CBDEE0FFEF28AC010022C19445DF326E5C26C708C5EC0DA7D9985B02'),
  (24, '/post/board-list', NULL, NULL, '/ERFlow/post/boardList.jsp', '4D0B4292FC1A94E622F319AEFB34E9AD4270E32A898591F6DE299357A711C7E4'),
  (25, '/process/list', NULL, NULL, '/ERFlow/process/processList.jsp', 'F03BEAC83BFF5F1D13D55F26C10040BED42E0C5BBAC8EC850574D03C13AE8CFF'),
  (26, '/process/register', NULL, NULL, '/ERFlow/process/processRegister.jsp', 'F03BEAC83BFF5F1D13D55F26C10040BED42E0C5BBAC8EC850574D03C13AE8CFF'),
  (27, '/product/ingredient-product', NULL, NULL, '/ERFlow/product/ingredientProduct.jsp', 'BC58BCABC3239263B4D3838A0673BA319C845BEAD96EFFEF412C3349960FE0B2'),
  (28, '/product/processed-product', NULL, NULL, '/ERFlow/product/processedProduct.jsp', '5C5D1BE8247DEADA5D073A263A19B4E02240BB7A6D2E8087723AEA0783B68C0C'),
  (29, '/product/producted-product', NULL, NULL, '/ERFlow/product/productedProduct.jsp', 'FD9B4A29561DE0669FF283D9ABE83004605B28ECBC9562A238F0E0B72994FFC1'),
  (30, '/profile', NULL, NULL, '/ERFlow/profile.jsp', '4A7849864C3B8D4067BC853C67BCFA572B7DFE68BBC773372BC14393B1F6C5B8'),
  (31, '/proposal/document', NULL, NULL, '/ERFlow/proposal/proposalDocument.jsp', '53A5157A4BAB5F1B75921C9B42888D7E1539466CD3DD258C1E318F29B62EC586'),
  (32, '/proposal/list', NULL, NULL, '/ERFlow/proposal/proposalList.jsp', '53A5157A4BAB5F1B75921C9B42888D7E1539466CD3DD258C1E318F29B62EC586'),
  (33, '/proposal/register', NULL, NULL, '/ERFlow/proposal/proposalRegister.jsp', '53A5157A4BAB5F1B75921C9B42888D7E1539466CD3DD258C1E318F29B62EC586'),
  (34, '/proposal/route-list', NULL, NULL, '/ERFlow/proposal/proposalRouteList.jsp', 'C6409ACB532D53B2C7F4065039A8E7FED426810370BCD39330526F662A115A72'),
  (35, '/proposal/route-register', NULL, NULL, '/ERFlow/proposal/proposalRouteRegister.jsp', 'C6409ACB532D53B2C7F4065039A8E7FED426810370BCD39330526F662A115A72'),
  (36, '/proposal/route-update', NULL, NULL, '/ERFlow/proposal/proposalRouteUpdate.jsp', 'C6409ACB532D53B2C7F4065039A8E7FED426810370BCD39330526F662A115A72'),
  (37, '/task/purchase-task', NULL, NULL, '/ERFlow/task/purchaseTask.jsp', '3879BC2C91F0F2F88E7468299B546E2F2098B09186750212D2247F73306B0A79'),
  (38, '/task/sell-task', NULL, NULL, '/ERFlow/task/sellTask.jsp', '5D2B70044459C29621CC45B55113F427F933BDE9D43404D387ED8585EFE64AD9'),
  (39, '/task/register', 'flag', 'sell', '/ERFlow/task/taskRegister.jsp', '5D2B70044459C29621CC45B55113F427F933BDE9D43404D387ED8585EFE64AD9'),
  (40, '/task/register', 'flag', 'purchase', '/ERFlow/task/taskRegister.jsp', '3879BC2C91F0F2F88E7468299B546E2F2098B09186750212D2247F73306B0A79'),
  (41, '/task/update', 'flag', 'sell', '/ERFlow/task/taskUpdate.jsp', '5D2B70044459C29621CC45B55113F427F933BDE9D43404D387ED8585EFE64AD9'),
  (42, '/task/update', 'flag', 'purchase', '/ERFlow/task/taskUpdate.jsp', '3879BC2C91F0F2F88E7468299B546E2F2098B09186750212D2247F73306B0A79'),
  (43, '/unit/delete-proc', NULL, NULL, '/ERFlow/unit/unitDeleteProc.jsp', '8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2'),
  (44, '/unit/list', NULL, NULL, '/ERFlow/unit/unitList.jsp', '8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2'),
  (45, '/unit/register', NULL, NULL, '/ERFlow/unit/unitRegister.jsp', '8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2'),
  (46, '/unit/register-proc', NULL, NULL, '/ERFlow/unit/unitRegisterProc.jsp', '8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2'),
  (47, '/unit/update', NULL, NULL, '/ERFlow/unit/unitUpdate.jsp', '8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2'),
  (48, '/unit/update-proc', NULL, NULL, '/ERFlow/unit/unitUpdateProc.jsp', '8A4364846CD2FC493883860129E7B91E800820F895EDC9DDEDE4CC10E8389BC2'),
  (49, '/work', NULL, NULL, '/ERFlow/work/work.jsp', 'B02E912CD7BFFC1760E1C5C009F97386770E5A280C11425C4F94A7B2D11554EA');

INSERT INTO `menu` (`menu_id`, `placement`, `parent_id`, `sort_order`, `label`, `visibility`, `icon`, `separator_before`, `url`, `screen_id`) VALUES
  (1, 'SIDE', NULL, 1, '문서 관리', 'ALWAYS', NULL, 0, NULL, NULL),
  (2, 'SIDE', 1, 1, '기안 작성', 'ALWAYS', NULL, 0, '/document/register', 21),
  (3, 'SIDE', 1, 2, '문서 리스트', 'ALWAYS', NULL, 0, '/document/list', 20),
  (4, 'SIDE', NULL, 2, '전자결재', 'ALWAYS', NULL, 0, NULL, NULL),
  (5, 'SIDE', 4, 1, '결재 리스트', 'ALWAYS', NULL, 0, '/proposal/list', 32),
  (6, 'SIDE', 4, 2, '결재라인 관리', 'ALWAYS', NULL, 0, '/proposal/route-list', 34),
  (7, 'SIDE', NULL, 3, '생산관리', 'ALWAYS', NULL, 0, NULL, NULL),
  (8, 'SIDE', 7, 1, '생산 설비 관리', 'ALWAYS', NULL, 0, '/unit/list', 44),
  (9, 'SIDE', 7, 2, '공정 관리', 'ALWAYS', NULL, 0, '/process/list', 25),
  (10, 'SIDE', NULL, 4, '구매', 'ALWAYS', NULL, 0, NULL, NULL),
  (11, 'SIDE', 10, 1, '협력업체 관리', 'ALWAYS', NULL, 0, '/company/list?flag=1', 13),
  (12, 'SIDE', 10, 2, '원재료 관리', 'ALWAYS', NULL, 0, '/product/ingredient-product', 27),
  (13, 'SIDE', 10, 3, '가공품 관리', 'ALWAYS', NULL, 0, '/product/processed-product', 28),
  (14, 'SIDE', 10, 4, '발주 관리', 'ALWAYS', NULL, 0, '/task/purchase-task', 37),
  (15, 'SIDE', 10, 5, '입고 제품 관리', 'ALWAYS', NULL, 0, '/bound/inbound', 11),
  (16, 'SIDE', NULL, 5, '영업', 'ALWAYS', NULL, 0, NULL, NULL),
  (17, 'SIDE', 16, 1, '협력업체 관리', 'ALWAYS', NULL, 0, '/company/list?flag=0', 14),
  (18, 'SIDE', 16, 2, '수주 관리', 'ALWAYS', NULL, 0, '/task/sell-task', 38),
  (19, 'SIDE', 16, 3, '출고 제품 관리', 'ALWAYS', NULL, 0, '/bound/outbound', 12),
  (20, 'SIDE', 16, 4, '완제품 재고 관리', 'ALWAYS', NULL, 0, '/product/producted-product', 29),
  (21, 'SIDE', NULL, 6, '근태 관리', 'ALWAYS', NULL, 0, NULL, NULL),
  (22, 'SIDE', 21, 1, '근태 확인', 'ALWAYS', NULL, 0, '/work', 49),
  (23, 'SIDE', NULL, 7, '게시판', 'ALWAYS', NULL, 0, '/post/board-list', 24),
  (24, 'HEADER', NULL, 1, '설정', 'ADMIN', 'fa-solid fa-gear', 0, '/admin', NULL),
  (25, 'HEADER', NULL, 2, '쪽지', 'ALWAYS', 'fa-regular fa-envelope fa-lg', 0, '/message', 23),
  (26, 'HEADER', NULL, 3, '프로필', 'ALWAYS', 'fa-solid fa-user-tie fa-lg', 0, '/profile', 30),
  (27, 'HEADER', NULL, 4, '로그아웃', 'ALWAYS', NULL, 1, '/login/logout-proc', NULL);
