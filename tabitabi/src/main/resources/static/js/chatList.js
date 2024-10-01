const chatSender = $("#sender").val();

function showChatList(button) {
   const chatRoomId = button.value;
   const popupUrl = `/chatLists?id=${chatRoomId}`
   window.open(popupUrl, "popupWindow", "width=800,height=600,resizable=no");
}
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
function searchProducts() {
    const input = document.getElementById('searchInput').value.toLowerCase();
    
    const productList = document.querySelectorAll('.chat-list li');

    productList.forEach(function(li) {
        const productName = li.querySelector('#productname').textContent.toLowerCase();
        
        if (productName.includes(input)) {
            li.style.display = '';
        } else {
            li.style.display = 'none';
        }
    });
}

/*
function showChatList(button) {
    const chatRoomId = button.value;
    console.log("showChatList 아이디: ", chatRoomId);

    const formData = new FormData();
    formData.append('chatRoomId', chatRoomId);
    console.log("formData: ", formData);

    fetch('/showChatList', {
        method: 'POST',
        body: formData
    })
    .then(response => {
		console.log("파싱 전 데이터:",response);
        return response.json();
    })
    .then(data => {
		console.log("로그",data);
        updateChatList(data, chatRoomId);
    })
    .catch(error => {
        console.error('오류 발생:', error);
    });
}
  
function updateChatList(data, chatRoomId) {
    const chatListElement = document.getElementById('chat');
    chatListElement.innerHTML = '';
	// const chatRoomId = data.length > 0 ? data[0].chatRoomId : '';
	console.log("Id: ",chatRoomId);
    const chatRoomHTML = `
        <div id="chat">
        	<input type="hidden" id="chatRoom" value="${chatRoomId}"/>
            <input type="text" id="message" placeholder="Your message" />
			<button onclick="sendMessage()">Send</button>
            <div id="chat">
                <h2>Messages:</h2>
                <div id="messages">
                    ${data.map(message => `<div>${message.sender}: ${message.content}</div>`).join('')}
                </div>
            </div>
        </div>
    `;
    chatListElement.innerHTML = chatRoomHTML;
}
*/