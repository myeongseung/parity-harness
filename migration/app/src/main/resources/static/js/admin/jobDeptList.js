$(function() {
   $('.open-job-btn').on('click', openNewJob);
   $('.open-dept-btn').on('click', openNewDept);

   $('[name=jobKeyword]').on('keydown', function(e) {
      if ($('[name=jobKeyword]').is(':focus') && e.which === 13) {
         e.preventDefault();
         $('[name=readFrm]').find('[name=jobKeyword]').val($('[name=jobFrm]').find('[name=jobKeyword]').val());
         $('[name=readFrm]').submit();
      }
   });
   
   $('[name=deptKeyword]').on('keydown', function(e) {
      if ($('[name=deptKeyword]').is(':focus') && e.which === 13) {
         e.preventDefault();
         $('[name=readFrm]').find('[name=deptKeyword]').val($('[name=deptFrm]').find('[name=deptKeyword]').val());
         $('[name=readFrm]').submit();
      }
   });

   // 검색 버튼 클릭 이벤트 핸들러
   $('[name=jobms]').on('click', function() {
      $('[name=readFrm]').find('[name=jobKeyword]').val($('[name=jobFrm]').find('[name=jobKeyword]').val());
      $('[name=readFrm]').submit();
   });
   
   $("#chkAllJob").click(function() {
      $("input[name=jobId]").prop("checked", $("#chkAllJob").is(":checked"));
   });

   $("input[name=jobId]").click(function() {
      const total = $("input[name=jobId]").length;
      const checked = $("input[name=jobId]:checked").length;

      $("#chkAllJob").prop("checked", total === checked);
   });

   $("#chkAllDept").click(function() {
      $("input[name=deptId]").prop("checked", $("#chkAllDept").is(":checked"));
   });

   $("input[name=deptId]").click(function() {
      const total = $("input[name=deptId]").length;
      const checked = $("input[name=deptId]:checked").length;

      $("#chkAllDept").prop("checked", total === checked);
   });
   
  $('[name=deptms]').on('click', function() {
      $('[name=readFrm]').find('[name=deptKeyword]').val($('[name=deptFrm]').find('[name=deptKeyword]').val());
      $('[name=readFrm]').submit();
   });
   
   function openNewJob() {
      const width = 400;
      const height = 220;
      const left = (screen.width - width) / 2;
      const top = (screen.height - height) / 2;
      const features = `width=${width},height=${height},left=${left},top=${top}`;

      window.open('jobRegister.jsp', '_blank', features);
   }

   function openNewDept() {
      const width = 500;
      const height = 420;
      const left = (screen.width - width) / 2;
      const top = (screen.height - height) / 2;
      const features = `width=${width},height=${height},left=${left},top=${top}`;

      window.open('deptRegister.jsp', '_blank', features);
   }
});