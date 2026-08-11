function selectBank(bankCode, bankName) {
	window.opener.receiveBankInfo(bankCode, bankName);
	window.close(); // 팝업 창을 닫습니다.
}