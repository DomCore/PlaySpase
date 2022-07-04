var stompClient = null;
var username = null;
var logo = null;
var checked = false;

  chat = {
    container: document.querySelector('.container .right'),
    current: null,
    person: null,
    name: document.querySelector('.container .right .top .name')
  }


connect();

function connect() {
  username = document.querySelector('#name').value.trim();
  if (username) {
    var socket = new SockJS('/javatechie');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
  }
}


function onConnected() {
  // Subscribe to the Public Topic
  stompClient.subscribe('/topic/public', onMessageReceived);

  // Tell your username to the server
  stompClient.send("/app/chat.register",
    {},
    JSON.stringify({sender: username, receiver: null, type: 'JOIN'})
  )
}
function onError(error) {
  connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
  connectingElement.style.color = 'red';
}
function onMessageReceived(payload) {
  var message = JSON.parse(payload.body);
  if (message.receiver === username && message.type === "CHAT") {
  beep();
  }
}

function beep() {
  var aud = new Audio();
  aud.src = 'https://cdn.pixabay.com/audio/2022/02/22/audio_d1718ab41b.mp3';
  aud.play();
}

