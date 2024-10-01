let stompClient = null;

const messageInput = document.getElementById('message');
addEventListener('keydown', (e) => {
   if (e.key === 'Enter') {
      sendMessage();
   }
});

function back() {
   window.history.back();
}

const images = ['/images/free-icon-apple-3137044.png',
   '/images/free-icon-dragon-fruit-2224312.png',
   '/images/free-icon-peach-454443.png',
   '/images/free-icon-strawberry-4587687.png'];

function displayRandomImage() {
   const randomImageElements = document
      .getElementsByClassName('randomImage');

   for (let i = 0; i < randomImageElements.length; i++) {
      const randomIndex = Math.floor(Math.random() * images.length);
      const randomImageUrl = images[randomIndex];
      randomImageElements[i].src = randomImageUrl;
   }
}

window.onload = displayRandomImage;

function connect() {
   var socket = new SockJS('/ws');
   stompClient = Stomp.over(socket);
   stompClient.connect({}, function(frame) {
      console.log('Connected: ' + frame);
      stompClient.subscribe('/topic/messages', function(messageOutput) {
         const message = JSON.parse(messageOutput.body);
         const currentChatRoomId = document.getElementById('chatRoom').value;
         console.log("message:", message);
         console.log("chatRoomId:", currentChatRoomId);
         if (message.chatRoomId == currentChatRoomId) {
            showMessage(message, message.sender);
         }
      });
   });
}

function sendMessage() {
   const chatRoomId = document.querySelector('#chatRoom').value;
   const sender = document.getElementById('sender').value.trim();
   const content = document.getElementById('message').value.trim();
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

function showMessage(message, senderName) {
   const messagesDiv = document.getElementById('chatBody');
   const chatSender = $("#sender").val();

   const messageContainer = document.createElement('div');
   messageContainer.className = 'message-container';

   const oneMessage = document.createElement('div');
   oneMessage.className = 'oneMessage';

   const messageBox = document.createElement('div');
   messageBox.className = 'message-box';
   messageBox.textContent = message.content;
   const messageTime = document.createElement('span');
   function formatDate(date) {
      const d = new Date(date);

      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');

      const formattedTime = d.toLocaleTimeString(undefined, {
         hour: '2-digit',
         minute: '2-digit',
         hour12: true
      })

      return `${month}/${day} ${formattedTime}`;
   }
   messageTime.textContent = formatDate(message.createdTime);
   if (senderName === chatSender) {
      messageContainer.className = 'right';
      oneMessage.className = 'oneMessage';
      messageTime.className = 'message-right';
   } else {
      messageContainer.className = 'left';
      oneMessage.className = 'oneMessage';
      messageTime.className = 'message-left';
   }

   oneMessage.appendChild(messageBox);
   oneMessage.appendChild(messageTime);

   messageContainer.appendChild(oneMessage);
   messagesDiv.appendChild(messageContainer);

   messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

connect();