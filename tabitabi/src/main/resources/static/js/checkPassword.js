function setPw() {
    const pw = $('#password').val();
    const rePw = $('#re-password').val();

    // 비밀번호 길이 검사
    if (pw.length < 5 || pw.length > 20) {
        alert("비밀번호는 5자 ~ 20자 사이로 입력해 주세요.");
        return false;
    }
    
    // 비밀번호 일치 검사
    if (pw === rePw) {
        $.ajax({
            type: "POST",
            data: {
                "password": pw
            },
            success: function(data) {
                // 변경 성공 시 알림창 크기 조정
                alert("정상적으로 변경되었습니다.");
                
                // 팝업 닫기
                window.close(); // 팝업을 닫음
                
                // 부모 창으로 돌아가기
                window.opener.location.reload(); // 부모 창 새로 고침
            },
            error: function(error) {
                alert("다시 입력해주세요");
            }
        });    
    } else {
        alert("비밀번호가 다릅니다.");
        return false;
    }
}
