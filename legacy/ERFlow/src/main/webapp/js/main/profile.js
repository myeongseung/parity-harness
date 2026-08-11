$(function() {
   $('.message-btn').on("click", function() { /* 쪽지쓰기 버튼 클릭 */
      const replyWindow = window.open('message/register.jsp', '_blank',
         'width=600, height=550, scrollbars=no, resizable=no, toolbar=no, menubar=no, channelmode=no');
      const replyValue = $(this).data('id');

      replyWindow.onload = function() {
         let receiverElement = replyWindow.document.getElementById('receiverId');

         if (receiverElement) {
            let buttonElement = replyWindow.document.getElementsByClassName('search-message');

            receiverElement.value = replyValue;
            buttonElement[0].disabled = true;
         }
      }
   });

   $('#search-button').on('click', function() {
      const width = 660;
      const height = 800;
      const left = (screen.width - width) / 2;
      const top = (screen.height - height) / 2;
      const features = `width=${width},height=${height},left=${left},top=${top}`;

      window.open('findEachUser.jsp', '_blank', features);
   });
   
   $('#monthPicker').on('change', function() {
      $('[name=readFrm]').find('[name=date]').val($('#monthPicker').val());
      $('[name=readFrm]').submit();
   });
});

function receiveEachUserInfo(id) {
   document.readFrm.id.value = id;
   readFrm.submit();
}