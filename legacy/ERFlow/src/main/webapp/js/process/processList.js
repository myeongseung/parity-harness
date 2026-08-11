$(function() {
	$(".main-search-text[name='keyword']").on("keydown", function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
			$(".main-search-icon i").trigger("click");
		}
	});

	$(".main-search-icon i").on("click", function() {
		let _data = ["keyfield", "keyword"];
		_data.forEach(function(key_name) {
			$("form[name='readFrm']").find('[name=' + key_name + ']').val($("form[name='processFrm']").find('[name=' + key_name + ']').val());
		});
		$("form[name='readFrm']").submit();
	});
	
	$("#delete-button").on("click", function() {
		if ($('[name=processId]:checked').length == 0) {
			alert('삭제할 공정을 선택해주세요.');
		} else {
			$('[name=processFrm]').submit();
		}
	});
	
	$("#register-button").on("click", function() {
		document.location.href = "processRegister.jsp";
	});
	
	$('#chkAll').on('click', function() {
		$('input[name=processId]').prop('checked', $('#chkAll').is(':checked'));
	});

	$('[name=processId]').on('click', function() {
		const total = $('input[name=processId]').length;
		const checked = $('input[name=processId]:checked').length;

		$("#chkAll").prop("checked", total === checked);
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
