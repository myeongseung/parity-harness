$(function() {
	$('#delete-button').on('click', function() {
		if ($('[name=proposalId]:checked').length == 0) {
			alert('삭제할 결제라인을 선택해주세요.');
		} else {
			$('[name=proposalFrm]').submit();
		}
	});
	
	$('#register-button').on('click', function() {
		document.location.href = 'proposalRouteRegister.jsp'
	});

	$(".main-search-text[name='keyword']").on("keydown", function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
			$(".main-search-icon i").trigger("click");
		}
	});
	
	$(".main-search-icon i").on("click", function() {
		let _data = [ "keyfield", "keyword" ];
		_data.forEach(function(key_name) {
			$("form[name='readFrm']").find('[name=' + key_name + ']').val( $("form[name='proposalFrm']").find('[name='+ key_name + ']').val() );
		});
		$("form[name='readFrm']").eq(0).submit();
	});
	
	$("#chkAll").click(function() {
		$('[name=proposalId]').prop('checked', $('#chkAll').is(':checked'));
	});

	$('[name=proposalId]').click(function() {
		const total = $('[name=proposalId]').length;
		const checked = $('[name=proposalId]:checked').length;

		$('#chkAll').prop('checked', total === checked);
	});
});

function block(base, blk) {
   document.readFrm.nowPage.value = base * (blk - 1) + 1;
   document.readFrm.submit();
}

function paging(page) {
   document.readFrm.nowPage.value = page;
   document.readFrm.submit();
}