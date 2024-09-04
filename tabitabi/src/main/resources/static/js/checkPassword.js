function setPw(){
	const pw= $('#password').val();
	const rePw= $('#re-password').val();
	if (pw.length <= 4 || pw.length >= 21) {
 	alert("비밀번호는 5자 ~ 20자 사이로");
	return false;
	}
	
	if(pw == rePw){
		$.ajax({
  		type: "POST",
  		data: {
			"password": pw
		  },
  		success: function(data){
			  alert("정상적으로 변경되었습니다.");
			  window.location.href = "/";
		  },
		error: function(error) {
			alert("다시 입력해주세요");
		}
		});	
	}else{
	alert("비밀번호가 다릅니다");
	return false;
	}
}