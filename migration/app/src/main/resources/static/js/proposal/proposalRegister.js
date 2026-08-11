$(function() {	
	$("#find-document").on("click", function() {
    const width = 584;
		const height = 400;
		const left = (screen.width - width) / 2;
		const top = (screen.height - height) / 2;
		const features = `width=${width},height=${height},left=${left},top=${top}`;
		
     	 window.open('../findDocument.jsp', '_blank', features);
    });
    
	$("#find-route").on("click", function() {
		const width = 650;
		const height = 400;
		const left = (screen.width - width) / 2;
		const top = (screen.height - height) / 2;
		const features = `width=${width},height=${height},left=${left},top=${top}`;

		window.open('../findProposalRoute.jsp', '_blank', features);
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
	document.readFrm.documentId.value = document.proposalFrm.documentId.value;
	document.readFrm.submit();
}

function receiveDocumentInfo(documentId, documentName) {
	console.log(documentId + " : " + documentName)
	document.proposalFrm.documentId.value = documentId;
	document.proposalFrm.documentName.value = documentName;
}

function receiveRouteInfo(routeId, route, nickname ) {
	document.proposalFrm.routeId.value = routeId;
	document.proposalFrm.nickname.value = nickname;
	document.proposalFrm.route.value = route;
}