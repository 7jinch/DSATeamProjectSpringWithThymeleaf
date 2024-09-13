function startChat() {
 	const product = document.getElementById('product').value;
    const seller = document.getElementById('seller').value;
    console.log(product);
    console.log(seller);
    const params = new URLSearchParams({
        product: product,
        seller: seller
    });

    fetch('/createChatRoom', {
             method: 'POST',
             headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
             },
             body: params.toString()
            })
            .then(response => response.text())
            .then(data => {
				console.log("data:",data);
                if (data) {
					    const chatRoomId = encodeURIComponent(data);
    					window.location.href = `/member/chat?chatRoomId=${chatRoomId}`;
                } else {
                    alert("채팅방 생성에 실패했습니다.");
                }
            })
            .catch(error => console.error('Error:', error));
        }