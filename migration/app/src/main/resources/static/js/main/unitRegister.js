$(document).ready(function() {
	$(".find-document").on("click", function() {
		const width = 584;
		const height = 400;
		const left = (screen.width - width) / 2;
		const top = (screen.height - height) / 2;
		const features = `width=${width},height=${height},left=${left},top=${top}`;

		window.open('../findDocument.jsp', '_blank', features);
	});

	$(".find-user").on("click", function() {
		const width = 650;
		const height = 400;
		const left = (screen.width - width) / 2;
		const top = (screen.height - height) / 2;
		const features = `width=${width},height=${height},left=${left},top=${top}`;

		window.open('../findUser.jsp', '_blank', features);
	});
});

function updateSelect(selectValue) {
		document.unitFrm.userId.value = selectValue;
}

function receiveDocumentInfo(documentId) {
	document.unitFrm.documentId.value = documentId;
}