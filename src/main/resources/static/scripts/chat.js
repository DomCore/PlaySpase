document.querySelector('.chat').classList.add('active-chat')
document.querySelector('.person').classList.add('active')
var stompClient = null;
var username = null;
var logo = null;
var checked = false;
var receiver = document.querySelector('.user').value.trim();
var file = document.querySelector('#file');
var fileData = null;

let friends = {
    list: document.querySelector('ul.people'),
    all: document.querySelectorAll('.left .person'),
    name: ''
  },
  chat = {
    container: document.querySelector('.container .right'),
    current: null,
    person: null,
    name: document.querySelector('.container .right .top .name')
  }

friends.all.forEach(f => {
  f.addEventListener('mousedown', () => {
    f.classList.contains('active') || setAciveChat(f)
  })
});
function setAciveChat(f) {
  friends.list.querySelector('.active').classList.remove('active')
  f.classList.add('active')
  try {
    f.classList.remove('blinking');
  } catch (e){}
  chat.current = chat.container.querySelector('.active-chat')
  chat.person = f.getAttribute('data-chat')
  receiver = chat.person;
  chat.current.classList.remove('active-chat')
  check();
  chat.container.querySelector('[data-chat="' + chat.person + '"]').classList.add('active-chat')
  friends.name = f.querySelector('.name').innerText
  chat.name.innerHTML = friends.name
  chat.name.setAttribute("href","/user/profile?name=" + friends.name)
  chat.container.querySelector('[data-chat="' + chat.person + '"]').scrollTop = chat.container.querySelector('[data-chat="' + chat.person + '"]').scrollHeight
}
try {
  document.querySelector('.chat').scrollTop = document.querySelector('.chat').scrollHeight;
} catch (e) {

}

var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var connectingElement = document.querySelector('.connecting');



connect();

function connect() {
  username = document.querySelector('#name').value.trim();
  logo = document.querySelector('#logo').value.trim();

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
    JSON.stringify({sender: username, receiver: receiver, type: 'JOIN'})
  )
  check();
}


function onError(error) {
  connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
  connectingElement.style.color = 'red';
}

if (window.File && window.FileReader && window.FileList && window.Blob) {
  file.addEventListener('change', handleFileSelect, false);
} else {
  alert('The File APIs are not fully supported in this browser.');
}


function send(event) {
  var messageContent = messageInput.value.trim();

  if ((messageContent || fileData) && stompClient) {
    var chatMessage = {
      sender: username,
      receiver: receiver,
      content: messageInput.value,
      type: 'CHAT',
      file: fileData
    };

    stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
    messageInput.value = '';
    fileData = null;

  }
  try {
    event.preventDefault();
  } catch (e) {

  }
}

function handleFileSelect(evt) {
  var f = evt.target.files[0]; // FileList object
  var reader = new FileReader();
  // Closure to capture the file information.
  reader.onload = (function(theFile) {
    return function(e) {
      var binaryData = e.target.result;
      //Converting Binary Data to base 64
      var base64String = window.btoa(binaryData);
      //showing file converted to base64
      fileData = base64String;
      messageInput.value = '';
      send();
    };
  })(f);
  reader.readAsBinaryString(f);
}

function onMessageReceived(payload) {
  var message = JSON.parse(payload.body);
  if (message.sender === receiver && message.receiver === username || message.sender === username && message.receiver === receiver) {
    if (message.type === 'JOIN') {
    } else if (message.type === 'LEAVE') {
    } else {
      if (message.file != null && message.file.length > 0) {
        var messageElement = document.createElement('img');
        messageElement.classList.add('user_image');
        messageElement.src = 'data:image/png;base64,' + message.file;
      } else {
        var messageElement = document.createElement('div');
      }
      messageElement.classList.add('bubble');
      if (message.sender === username && !message.system) {
        messageElement.classList.add('me');
        document.querySelector('[data-chat-friend="' + message.receiver + '"]').querySelector('.preview').textContent = message.content;
        document.querySelector('[data-chat-friend="' + message.receiver + '"]').querySelector('.time').textContent = message.time;
      } else if (message.receiver === username && !message.system){
        messageElement.classList.add('you');
        check();
        smallBeep();
        document.querySelector('[data-chat-friend="' + message.sender + '"]').querySelector('.preview').textContent = message.content;
        document.querySelector('[data-chat-friend="' + message.sender + '"]').querySelector('.time').textContent = message.time;
      } else if (message.sender === receiver && message.system) {
        messageElement.classList.add('system');
        check();
        smallBeep();
        document.querySelector('[data-chat-friend="' + message.sender + '"]').querySelector('.preview').textContent = message.content;
        document.querySelector('[data-chat-friend="' + message.sender + '"]').querySelector('.time').textContent = message.time;
      }
      var messageText = document.createTextNode(message.content);
      messageElement.appendChild(messageText);
      try {
        chat.container.querySelector('[data-chat="' + chat.person + '"]').scrollTop = chat.container.querySelector('[data-chat="' + chat.person + '"]').scrollHeight;
        chat.container.querySelector('[data-chat="' + chat.person + '"]').appendChild(messageElement);
        chat.container.querySelector('[data-chat="' + chat.person + '"]').scrollTop = chat.container.querySelector('[data-chat="' + chat.person + '"]').scrollHeight;
      } catch (e) {
        document.querySelector('.chat').appendChild(messageElement);
        document.querySelector('.chat').scrollTop = document.querySelector('.chat').scrollHeight;
      }
    }
  }
  if (message.sender !== receiver && message.receiver === username) {
    document.querySelector('[data-chat="' + message.sender + '"]').classList.add('blinking');
    beep();
    document.querySelector('[data-chat-friend="' + message.sender + '"]').querySelector('.preview').textContent = message.content;
    document.querySelector('[data-chat-friend="' + message.sender + '"]').querySelector('.time').textContent = message.time;

    if (message.file != null && message.file.length > 0) {
      var messageElement = document.createElement('img');
      messageElement.classList.add('user_image');
      messageElement.src = 'data:image/png;base64,' + message.file;
    } else {
      var messageElement = document.createElement('div');
      messageElement.classList.add('bubble');
      var messageText = document.createTextNode(message.content);
      messageElement.appendChild(messageText);
    }
    messageElement.classList.add('you');

    document.querySelector('[data-chat-main="' + message.sender + '"]').appendChild(messageElement);
    document.querySelector('[data-chat-main="' + message.sender + '"]').scrollTop = chat.container.querySelector('[data-chat-main="' + chat.person + '"]').scrollHeight;
  }
}


messageForm.addEventListener('submit', send, true);

function check() {
  var chatMessage = {
    sender: username,
    receiver: receiver,
    content: messageInput.value,
    type: 'CHAT',
    checked: true
  }
  return stompClient.send("/app/chat.check", {}, JSON.stringify(chatMessage));
}
function isChecked() {
  var chatMessage = {
    sender: username,
    receiver: receiver,
    content: messageInput.value,
  }
  return stompClient.send("/app/chat.check", {}, JSON.stringify(chatMessage));
}

function beep() {
  var aud = new Audio();
  aud.src = 'https://cdn.pixabay.com/audio/2022/02/22/audio_d1718ab41b.mp3';
  aud.play();
}


  function smallBeep() {
    var aud = new Audio();
    aud.src = 'https://cdn.pixabay.com/audio/2021/08/04/audio_bb630cc098.mp3';
    aud.play();
  }
