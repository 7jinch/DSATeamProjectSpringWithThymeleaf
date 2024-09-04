function updateDotComValue(){
	   const selectEmail = $('#selectEmail').val();
	   if(selectEmail !== 'select'){
	      $('#dotCom').val(selectEmail);
	   }
	   if(selectEmail === 'select'){
	      $('#dotCom').val('');
	   }
	}
function updateQuestionValue() {
    const selectedValue = $('#question').val();
    if (selectedValue) {
        $('#selected-question').val(selectedValue);
    } else {
        $('#selected-question').val('');
    }
}
function joinFormValidator(){
	//let, const, var 차이 -> var 전역함수 ,let 업데이트는 가능 , const 업뎃:재선언 불가
	let email = "";
    if($('#selectEmail').val() !== 'select'){
       email = $('#email').val() + "@" + $('#selectEmail').val(); // 이메일
    } else {
       email = $('#email').val() + "@" + $('#dotCom').val(); // 이메일
    }
    const code = $('#VerifyCode').text();
    const password = $('#password').val(); // 비밀번호
    const name = $('#name').val(); // 이름
    const phoneNumber = $('#phoneNumber').val(); // 연락처
    const gender = $("input[name='gender']:checked").val(); // 성별
    const birth = $('#birth').val(); // 생년월일

    const now = new Date(); // 현재 날짜
    const birthDate = new Date(birth); // 입력받은 생년월일 날짜 포매팅하기
      
    const reg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // 이메일 정규표현식
    let errorMessage = "";
      
    const question = $('#selected-question').val();
	const answer = $('#answer').val();
    // 유효성 검사
    if($('#email').val().length <= 3 || $('#email').val().length >= 21) {
       clearError();
       errorMessage = "이메일은 4 ~ 20자 사이로 입력해 주세요.";
       $('#error-email').text(errorMessage);
       $('#email').focus();
       return false;
    } 
    //else if(code != 'true') {
    //   clearError();
    //   errorMessage = "이메일 인증을 해주세요";
    //   $('#error-emailCode').text(errorMessage);
    //   $('#emailCode').focus();
    //   return false;
    //} 
    else if(!RegExp('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$').test(email)){
       clearError();
       errorMessage = "이메일 형식에 맞춰서 작성해 주세요.(예시: test@test.test)";
       $('#error-email').text(errorMessage);
       $('#email').focus();
       return false;
	}
    else if(password.length <= 3 || password.length >= 21) {
       clearError(); 
       errorMessage = "비밀번호는 4자 ~ 20자 사이로 입력해 주세요.";
       $('#error-password').text(errorMessage);
       $('#password').focus();
       return false;
    } else if(name == null || name == "" || name == " "){
       clearError();
       errorMessage = "이름을 입력해 주세요.";
       $('#error-name').text(errorMessage);
       $('#name').focus();
       return false;
    } else if(name.length <= 5 || name.length >= 21){
       clearError();
       errorMessage = "이름은 6자 ~ 20자 사이로 입력해 주세요.";
		$('#error-name').text(errorMessage);
       $('#name').focus();
       return false;
    } 
	else if(!RegExp('^\\d{3}-\\d{4}-\\d{4}$').test(phoneNumber)){
	 	clearError();
	 	errorMessage = "연락처를 정확하게 작성해 주세요.(예시: 000-0000-0000)";
	 	$('#error-phone-number').text(errorMessage);
	 	$('#answer').focus();
	 	return false;
	}    
    else if(gender !== "MALE" && gender !== "FEMALE"){
       clearError();
       errorMessage = "성별을 선택해 주세요.";
       $('#error-gender').text(errorMessage);
       $('input[name="gender"]').focus();
       return false;
    } else if(birthDate >= now || !birth){
       clearError();
       errorMessage = "과거 날짜 선택";
       $('#error-birth').text(errorMessage);
       $('#birth').focus();
       return false;
    }else if(question.length<2){
	 	clearError();
	 	errorMessage = "질문을 선택";
	 	$('#error-question').text(errorMessage);
		$('select[id="question"]').focus();
	 	return false;
	}else if(answer == null || answer.trim() == ""){
	 	clearError();
	 	errorMessage = "대답을 작성하시오";
	 	$('#error-answer').text(errorMessage);
	 	$('#answer').focus();
	 	return false;
	}
    $.ajax({
        type: "POST",
        data: {
        	"email": email,
        	"password": password,
        	"name": name,
        	"gender": gender,
        	"birth": birth,
  			"question": question,
  			"answer": answer,
  			"phoneNumber": phoneNumber
        },
        success: function(data){
            console.log(data);
            alert("정상적으로 가입되었습니다.");
            window.location.href = "/";
        },
        error: function(error, status, xhr){
           console.error("오류 발생:", error);
           console.error("상태 코드:", xhr.status); // 400 내(클라이언트)탓 500 니(서버)탓 확인용
        }
    });
}
function clearError(){
	   $('#error-email').text("");
	   $('#error-password').text("");
	   $('#error-name').text("");
	   $('#error-gender').text("");
	   $('#error-birth').text("");
	   $('#error-question').text("");
	   $('#error-answer').text("");
	   $('#error-phone-number').text("");
	}
function emailCodeSend(){
	let email = "";
    if($('#selectEmail').val() !== 'select'){
       email = $('#email').val() + "@" + $('#selectEmail').val(); // 이메일
    } else {
       email = $('#email').val() + "@" + $('#dotCom').val(); // 이메일
    }
    const mainUrl = window.location.origin+ window.location.pathname;
	$.ajax({
        type: "POST",
        url: mainUrl+"/emailCode",
        data: {
        	"email": email
        },
        success: function(response){
            alert("이메일이 전송되었습니다.");
            $('#emailCode').focus();
            const code= response.emailCode;
            $('#VerifyCode').text(code);
        },
        error: function(error, status, xhr){
           console.error("오류 발생:", error);
           console.error("상태 코드:", xhr.status); // 400 내(클라이언트)탓 500 니(서버)탓 확인용
        	if(error.status === 500){
				alert("중복된 이메일입니다.");
				$('#emailCode,#email,#emailSend,#codeVerify,#selectEmail,#dotCom').prop('disabled', false);
				$('#VerifyCode').text('');
			}
        }
    });
}
function emailCodeVerify(){
	const emailCode = $('#emailCode').val();
	const code = $('#VerifyCode').text();
	if(code!=emailCode){
		$('#error-emailCode').text("인증번호가 다릅니다.");
		return false;
	}
	$('#error-emailCode').text("인증되었습니다.");
	$('#VerifyCode').text('true');
	$('#emailCode,#email,#emailSend,#codeVerify,#selectEmail,#dotCom').prop('disabled', true);
}