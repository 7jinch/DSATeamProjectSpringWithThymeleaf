function updateQuestionValue() {
    const selectedValue = $('#question').val();
    if (selectedValue) {
        $('#selected-question').val(selectedValue);
    } else {
        $('#selected-question').val('');
    }
    console.log(selectedValue);
}
function FormValidator(){
	const email = $('#email').val();
	const question = $('#selected-question').val();
	const answer = $('#answer').val();
if($('#email').val().length <= 3 || $('#email').val().length >= 21) {
 alert("아이디는 4자 ~ 20자 사이로");
 $('#email').focus();
 return false;
}else if(question.length<2){
	 alert("질문을 선택");
	 $('select[id="question"]').focus();
	 return false;
}else if(answer == null || answer.trim() == ""){
	 alert("대답을 작성하시오");
	 $('#answer').focus();
	 return false;
}
$.ajax({
  type: "POST",
  data: {
  	"email": email,
  	"question": question,
  	"answer": answer
  },
  success: function(data){
      console.log(data);
      var currentUrl = window.location.origin + window.location.pathname;
      window.location.href = currentUrl + "/setPw";
  },
error: function(error, status, xhr) {
    console.log("Error details:", error);
	console.log("Status code:", error.status);
      if (error.status === 404) {
      alert("사용자를 찾을 수 없습니다.");
    } else if (error.status === 400) {
      alert("질문 또는 답변이 일치하지 않습니다.");
    } 
}
});	
}