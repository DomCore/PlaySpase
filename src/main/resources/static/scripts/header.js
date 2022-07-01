$('.navTrigger').click(function () {
  $(this).toggleClass('active');
  var mainListDiv =  $("#mainListDiv");
  mainListDiv.toggleClass("show_list");
  mainListDiv.fadeIn();
});