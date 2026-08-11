$(function() {
	$('#register-button').on('click', function() {
		document.location.href = 'proposalRegister.jsp'
	});
	
	$(".search-option select[name='keyfield']").on("change", function() {
	    $(".main-search-icon i").trigger("click");
	});
	
	$(".main-search-icon i").on("click", function() {
		let _data = [ "keyfield" ];
		_data.forEach(function(key_name) {
			$("form[name='readFrm']").find('[name=' + key_name + ']').val( $("form[name='proposalFrm']").find('[name='+ key_name + ']').val() );
		});
		$("form[name='readFrm']").submit();
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