var stompClient = null;

function connect() {
   var socket = new SockJS('/ws');
   stompClient = Stomp.over(socket);
   stompClient.connect({}, function(frame) {
      console.log('Connected: ' + frame);
      stompClient.subscribe('/topic/messages', function(messageOutput) {
          const message = JSON.parse(messageOutput.body);
          const currentChatRoomId = document.getElementById('chatRoom').value;
          console.log("message:",message);
          console.log("chatRoomId:",currentChatRoomId);
          if (message.chatRoomId == currentChatRoomId) {
                 showMessage(message);
          }
      });
   });
}

function sendMessage() {
	const chatRoomId = document.querySelector('#chatRoom').value;
	const sender = document.getElementById('sender').value.trim();
	const content = document.getElementById('message').value.trim();
	console.log("chatRoomId:",chatRoomId);
	console.log("sender:",sender);
	console.log("content:",content);
	if (!content) {
		console.error("Message content is empty!");
		alert("메시지 내용을 입력해 주세요.");
		return;
	}
	stompClient.send("/app/sendMessage", {}, JSON.stringify({
		'chatRoomId': chatRoomId,
		'sender': sender,
		'content': content
	}));
	document.getElementById('message').value = '';
}

function showMessage(message) {
	const messagesDiv = document.getElementById('messages');
	const messageElement = document.createElement('div');
	messageElement.textContent = message.sender + ": " + message.content;
	messagesDiv.appendChild(messageElement);
	messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

connect();