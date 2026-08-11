$(function () {
	$("#chkAll").click(function () {
		if ($("#chkAll").is(":checked")) {
			$("input[name=templateId]").prop("checked", true);
		} else {
			$("input[name=templateId]").prop("checked", false)
		};
	});

	$("input[name=templateId]").on('click', function() {
		const total = $("input[name=templateId]").length;
		const checked = $("input[name=templateId]:checked").length;
		
		if (total != checked) $("#chkAll").prop("checked", false);
		else $("#chkAll").prop("checked", true); 
	});
	
	$('#btn-register').on('click', function() {
		window.location.href = 'documentFormRegister.jsp';
	});
	
	$('.close-button').on("click", function() {
		 $('.modal').modal('hide');
	});
});