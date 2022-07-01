$('#password, #confirm_password').on('keyup', function () {
  if ($('#password').val() == $('#confirm_password').val()) {
    $('#message').html('*').css('color', '#03e9f4');
  } else
    $('#message').html('Пароли не совпадают').css('color', 'red');
});

let input = document.querySelector("#password");
let input2 = document.querySelector("#confirm_password");
let button = document.querySelector("#submit");
if (input && input2 && button) {
  button.disabled = true; //setting button state to disabled

  input.addEventListener("keyup", stateHandle);
  input2.addEventListener("keyup", stateHandle);
}

function stateHandle() {
  if ($('#password').val() != $('#confirm_password').val()) {
    button.disabled = true; //button remains disabled
  } else {
    button.disabled = false; //button is enabled
  }
}