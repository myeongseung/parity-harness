$(function() {
	$('.write-message').on("click", function() { /* 쪽지쓰기 버튼 클릭 */
		window.open('register.jsp', '_blank',
			'width=600, height=550, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
	});
	
	/* 답장쓰기 */
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
			
			// 답장하기 페이지 열기
			let replyWindow = window.open('reply.jsp', '_blank',
                    'width=600, height=550, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
			// 페이지가 로드될때 값 설정
			replyWindow.onload = function() {
				let receiverElement = replyWindow.document.getElementById('receiverId');
				
				if(receiverElement) {
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
		// 답장하기 페이지 열기
		let replyWindow = window.open('reply.jsp', '_blank',
                'width=600, height=550, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
		// 페이지가 로드될때 값 설정
		const replyValue = $('input[name=senderId]').val();
		
		replyWindow.onload = function() {
			let receiverElement = replyWindow.document.getElementById('receiverId');
			
			if(receiverElement) {
				receiverElement.value = replyValue;
			}
		}
    });
    
     
	$('td.read-message').on("click",function(){ /* messageIndex.jsp 에서 쪽지 내용 클릭 */
		messageId = $(this).closest('tr').find('input[name="messageId"]').val(); // messageId 값 넘겨주기
		window.open('read.jsp?messageId=' + messageId, '_blank', 
        'width=700, height=210, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
	});
	$('.search-message').on("click", function() { /* 답장 버튼 클릭 후 받는사람 찾기버튼 클릭 */
		window.open('../findUser.jsp', '_blank',
			'width=600, height=600, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
	});
	$('.close-message').on("click", function() { /* 답장이나 쪽지쓰기 버튼 클릭후 취소버튼 클릭 */
		window.close();
	});
	$('.delete-message').on("click", function() { /* 삭제버튼 클릭. */
		$('[name=messageFrm]').attr('action', 'deleteProc.jsp');
		$('[name=messageFrm]').find('[name=class]').val( $('[name=classFrm]').find('[name=class]').val() );
		$('[name=messageFrm]').attr('method', 'post').submit();	
	});
	
	$('.delete-message').on("click", function() { /* 쪽지 읽기에서 삭제버튼 클릭. */
		$('[name=readFrm]').attr('action', 'readDeleteProc.jsp');
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

	$("#chkAll").click(function() {   /* messageIndex.jsp 체크박스 전체 클릭*/
		if ($("#chkAll").is(":checked")) {
			$("input[name=messageId]").prop("checked", true);
		} else {
			$("input[name=messageId]").prop("checked", false)
		};
	});

	$("input[name=messageId]").click(function() {   /* messageIndex.jsp 체크박스 전체 클릭*/
		const total = $("input[name=messageId]").length;
		const checked = $("input[name=messageId]:checked").length;

		if (total != checked) $("#chkAll").prop("checked", false);
		else $("#chkAll").prop("checked", true);
	});
});


