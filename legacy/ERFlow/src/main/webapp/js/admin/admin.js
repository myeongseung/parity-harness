document.addEventListener('DOMContentLoaded', function() {
    var mainMenus = document.querySelectorAll('.sidebar-mainmenu');

    mainMenus.forEach(function(menu) {
        menu.addEventListener('click', function() {
            if (this.classList.contains('active')) {
                this.classList.remove('active');
            } else {
                // 현재 메뉴에 active 클래스 추가
                this.classList.add('active');
            }
        });
    });
});

$(function() {
	$('.logo-font2').on('click', function() {
		document.location.href = '/ERFlow/admin/admin.jsp';
	})
})