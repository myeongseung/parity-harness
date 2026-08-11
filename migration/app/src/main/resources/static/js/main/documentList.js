$(function() {
	$('#delete-button').on('click', function() {
		if ($('[name=docId]:checked').length == 0) {
			alert('삭제할 문서를 선택하세요.');
		} else {
			$('[name=docFrm]').submit();
		}
	});
	
	$('#register-button').on("click", function() {
		document.location.href = 'documentRegister.jsp'; 
	});
	
	$('#chkAll').on('click', function() {
		$('input[name=docId]').prop('checked', $('#chkAll').is(':checked'));
	});

	$('[name=docId]').on('click', function() {
		const total = $('input[name=docId]').length;
		const checked = $('input[name=docId]:checked').length;

		$("#chkAll").prop("checked", total === checked);
	});

	$('.main-search-text[name=keyword]').on('keydown', function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
			$('.main-search-icon i').trigger('click');
		}
	});
	
	$('.main-search-icon i').on('click', function() {
		const _data = [ "keyfield", "keyword" ];
		
		_data.forEach(function(_key) {
			$('form[name=readFrm]').find(`[name=${_key}]`).val( $('form[name=docFrm]').find(`[name=${_key}]`).val() );
		});
		$('form[name=readFrm]').submit();
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