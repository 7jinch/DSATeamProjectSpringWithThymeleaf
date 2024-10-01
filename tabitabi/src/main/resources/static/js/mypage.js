function updateQuestionValue() {
    const selectedValue = $('#question').val();
    if (selectedValue) {
        $('#selected-question').val(selectedValue);
    }
}
function removeSeller(){
	if(confirm("탈퇴하시겠습니까?")){
		const email = $("#email").val();
	    $.ajax({
	        type: "DELETE",
	        data: {
	        	"email": email
	        },
	        success: function(data){
	            console.log(data);
	            alert("탈퇴가 처리되었습니다.");
	            window.location.href = "/";
	        },
	        error: function(error, status, xhr){
	           console.error("회원 탈퇴 중 오류 발생:", error);
               console.error("상태 코드:", xhr.status); // 400 내(클라이언트)탓 500 니(서버)탓 확인용
	        }
	    });
	}
}

function pwCheck(){
	const email = $("#email").val();
	const password = $("#password").val();
	if(password.trim() === ''){
		alert("비밀번호를 입력하세요");
		$("#password").focus();
		return;
	}
	$.ajax({
        type: "POST",
        data: {
        	"email": email,
        	"password": password
        },
        success: function(data){
        	removeSeller(data);
        },
        error: function(error, status, xhr){
            console.error("회원 탈퇴 중 오류 발생:", error);
            console.error("상태 코드:", xhr.status); // 400인지 500인지 니탓인지 내탓인지 확인용
            alert("비밀번호가 틀렸다.");
            $("#password").focus();
        }
	});
};
function chooseImage(obj){
	const file = obj.files[0];
	if(!file.type.match("image.*")){
	     alert("이미지를 등록해야 합니다.")
	     obj.value = '';
	     return false;
	}
	
	let reader = new FileReader();
	reader.onload = function (e){ 
		  $('#imagePreview').attr("src",e.target.result);
	}
	reader.readAsDataURL(file);
}
function updateImage() {
	const image = $('#profile')[0];
    console.log(image);
	
    if (image.files.length === 0) {
        alert("파일을 선택해 주세요.");
        return;
    }
    
    let formData = new FormData();
    formData.append('profile', image.files[0]);
    
    $.ajax({
        type: "PUT",
        // 기본 컨텐츠 타입 설정(application/x-www-form-urlencoded; charset=UTF-8)
        // multipart/form-data 타입이 설정되어 있어야 하기 때문에
        contentType: false,
        // 객체가 이미 formdata로 인코딩 되어 있어 전송 중 변환 방지
        processData: false, 
        data: formData,
		success: function(data){
			alert("프로필이 수정되었습니다.");
			return;
		},
   		error: function(error, status, xhr){
        console.error("수정 오류 발생:", error);
        console.error("상태 코드:", xhr.status); // 400 내(클라이언트)탓 500 니(서버)탓 확인용
     	return false;
     	}
});
}
$(document).ready(function() {
        $('#updateForm').on('submit', function(event) {
            // updateForm 제출 방지
            event.preventDefault(); 

            const password = $('#password').val().trim();
			const name = $('#name').val().trim();
			const question = $('#selected-question').val();
			const answer = $('#answer').val().trim();
	
            if(password.length <= 4 || password.length >= 21) {
				alert("비밀번호는 5자 ~ 20자 사이로");				
       			$('#password').focus();
       			return false;
		    } else if(name == null || name == ""){
		       alert("이름은 공백일 수 없음");
		       $('#name').focus();
		       return false;
		    } else if(question.length<2){
			 	alert("질문을 선택");
				$('#question').focus();
			 	return false;
			}else if(answer == null || answer == ""){
			 	alert("대답을 작성하시오");
			 	$('#answer').focus();
			 	return false;
			}
            this.submit();
        });
    });