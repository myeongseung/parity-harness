$(function() {
	$("#find-user").on("click", function() {
		const width = 650;
		const height = 400;
		const left = (screen.width - width) / 2;
		const top = (screen.height - height) / 2;
		const features = `width=${width},height=${height},left=${left},top=${top}`;

		window.open('../findUser.jsp', '_blank', features);
	});

	jQuery("table > tbody").sortable({
		start: function(event, ui) {
			ui.item.addClass("selected");
		}
		, stop: function(event, ui) {
			ui.item.removeClass("selected");
		}
	});
	$("#myTable").on("click", ".delete-link", function() {
		$(this).parent().parent().remove();
	});

});

function updateSelect(selectValue) {
	document.readFrm.receiver.value = btoa(encodeURIComponent(selectValue));
	document.readFrm.nickname.value = document.proposalFrm.nickname.value;
	document.readFrm.submit();
}
