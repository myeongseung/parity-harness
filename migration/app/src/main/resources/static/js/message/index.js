// 쪽지 화면 스크립트.
// 출처: legacy/ERFlow/src/main/webapp/js/message/index.js
//
// 동작은 레거시 그대로다. 팝업·이동 주소만 새 라우트로 바꿨다(D-005).
//   register.jsp        -> /message/register
//   reply.jsp           -> /message/reply
//   read.jsp?messageId= -> /message/read?messageId=
//   ../findUser.jsp     -> /find/user
//   deleteProc.jsp      -> /message/delete
//   readDeleteProc.jsp  -> /message/read-delete
//
// block()/paging() 은 정의하지 않는다 — 레거시도 이 화면에는 두 함수를 두지 않아
// 페이징이 동작하지 않았다(D-045). 그대로 옮긴다.
$(function() {
	$('.write-message').on("click", function() { /* 쪽지쓰기 버튼 클릭 */
		window.open('/message/register', '_blank',
			'width=600, height=550, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
	});

	/* 답장쓰기 (목록에서) */
	$('.reply-message').on('click', function(e) {
		e.preventDefault();

		let idReplyValue = [];
		$('input[name=messageId]:checked').next().each(function() {
			idReplyValue.push($(this).val());
		});
		if (idReplyValue.length === 0) {
			alert('사원을 선택하세요.');
		} else if (idReplyValue.length === 1) {
			let replyValue = idReplyValue[0];

			let replyWindow = window.open('/message/reply', '_blank',
				'width=600, height=550, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
			replyWindow.onload = function() {
				let receiverElement = replyWindow.document.getElementById('receiverId');
				if (receiverElement) {
					receiverElement.value = replyValue;
				}
			}
		} else {
			alert('답장은 한명한테만 보낼수 있습니다.');
		}
	});

	/* 읽는 페이지에서 답장 */
	$('.reply-inner-message').on('click', function(e) {
		e.preventDefault();
		let replyWindow = window.open('/message/reply', '_blank',
			'width=600, height=550, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
		const replyValue = $('input[name=senderId]').val();

		replyWindow.onload = function() {
			let receiverElement = replyWindow.document.getElementById('receiverId');
			if (receiverElement) {
				receiverElement.value = replyValue;
			}
		}
	});

	$('td.read-message').on("click", function() { /* 쪽지 내용 클릭 -> 읽기 */
		messageId = $(this).closest('tr').find('input[name="messageId"]').val();
		window.open('/message/read?messageId=' + messageId, '_blank',
			'width=700, height=210, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
	});
	$('.search-message').on("click", function() { /* 받는사람 찾기 */
		window.open('/find/user', '_blank',
			'width=600, height=600, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
	});
	$('.close-message').on("click", function() { /* 취소 */
		window.close();
	});
	$('.delete-message').on("click", function() { /* 목록에서 삭제 */
		$('[name=messageFrm]').attr('action', '/message/delete');
		$('[name=messageFrm]').find('[name=class]').val($('[name=classFrm]').find('[name=class]').val());
		$('[name=messageFrm]').attr('method', 'post').submit();
	});

	$('.delete-message').on("click", function() { /* 쪽지 읽기에서 삭제 */
		$('[name=readFrm]').attr('action', '/message/read-delete');
		$('[name=readFrm]').attr('method', 'post').submit();
	});

	$(".main-search-text[name='keyword']").on("keydown", function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
			$("#message-search").trigger("click");
		}
	});

	$("#message-search").on("click", function() {
		$('form[name=classFrm]')
			.find('input[name=keyfield]')
			.val($('form[name=messageFrm]')
				.find('[name=keyfield]').val());

		$('form[name=classFrm]')
			.find('input[name=keyword]')
			.val($('form[name=messageFrm]')
				.find('[name=keyword]').val());

		$("form[name='classFrm']").submit();
	});

	$('.message-nav-sent-message').on('click', function() {
		const value = $('.message-nav-sent-message').text().trim();

		if (value === '받은쪽지함') {
			$('input[name=class]').val('receiver');
		} else if (value === '보낸쪽지함') {
			$('input[name=class]').val('sender');
		}
		$('[name=classFrm]').submit();
	});

	$("#chkAll").click(function() {
		if ($("#chkAll").is(":checked")) {
			$("input[name=messageId]").prop("checked", true);
		} else {
			$("input[name=messageId]").prop("checked", false)
		};
	});

	$("input[name=messageId]").click(function() {
		const total = $("input[name=messageId]").length;
		const checked = $("input[name=messageId]:checked").length;

		if (total != checked) $("#chkAll").prop("checked", false);
		else $("#chkAll").prop("checked", true);
	});
});
