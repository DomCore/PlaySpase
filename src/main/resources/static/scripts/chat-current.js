'use strict';

var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;
var logo = null;
var receiver = null;

connect();

function connect() {
  username = document.querySelector('#name').value.trim();
  receiver = document.querySelector('#receiver').value.trim();
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
    JSON.stringify({sender: username, receiver: receiver, logo: logo, type: 'JOIN'})
  )

  connectingElement.classList.add('hidden');
}


function onError(error) {
  connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
  connectingElement.style.color = 'red';
}


function send(event) {
  var messageContent = messageInput.value.trim();

  if (messageContent && stompClient) {
    var chatMessage = {
      sender: username,
      logo: logo,
      receiver: receiver,
      content: messageInput.value,
      type: 'CHAT'
    };

    stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
    messageInput.value = '';
  }
  event.preventDefault();
}


function onMessageReceived(payload) {
  var message = JSON.parse(payload.body);
  if (message.sender === receiver && message.receiver === username || message.sender === username && message.receiver === receiver) {
    if (message.type === 'JOIN') {
    } else if (message.type === 'LEAVE') {
    } else {
      var messageElement = document.createElement('li');
      messageElement.classList.add('chat-message');
      var avatarElement = document.createElement('img');
      avatarElement.src = message.logo;
      messageElement.appendChild(avatarElement);
      var usernameElement = document.createElement('span');
      var usernameText = document.createTextNode(message.sender);
      usernameElement.appendChild(usernameText);
      messageElement.appendChild(usernameElement);
      var textElement = document.createElement('p');
      var messageText = document.createTextNode(message.content);
      textElement.appendChild(messageText);
      messageElement.appendChild(textElement);
      messageArea.appendChild(messageElement);
      messageArea.scrollTop = messageArea.scrollHeight;
    }

  }
}


messageForm.addEventListener('submit', send, true)