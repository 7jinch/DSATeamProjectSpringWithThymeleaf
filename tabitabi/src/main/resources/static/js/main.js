const languageButton = document.querySelector('.btn-lang');
const relatedButton = document.querySelector('.btn-rel');
languageButton.addEventListener('click', () => {
  languageButton.classList.toggle('on');
})
relatedButton.addEventListener('click', () => {
  relatedButton.classList.toggle('on');
})

// main - dropdown
const dropBtn = document.querySelectorAll(".dropdown-btn");
const dropList = document.querySelectorAll(".dropdown-list");

for (let i = 0; i < dropBtn.length; i++) {
  dropBtn[i].addEventListener('click', function () {
    dropBtn[i].classList.toggle('on');
  })
}

for (let i = 0; i < dropBtn.length; i++) {
  dropList[i].addEventListener('click', (event) => {
    if (event.target.nodeName === "BUTTON") {
      dropBtn[i].innerText = event.target.innerText;
      dropBtn[i].classList.remove('on');
    }
  })
}

document.addEventListener('DOMContentLoaded', function() {
    const vid = document.querySelector('.vid');
    const elementA = document.querySelector('.header');
    const elementB = document.querySelector('.explore');
    const elementC = document.querySelector('.meet');

    document.addEventListener('scroll', function() {
        const video = vid.getBoundingClientRect();
        const rectA = elementA.getBoundingClientRect();
        const rectB = elementB.getBoundingClientRect();
        const rectC = elementC.getBoundingClientRect();
        
        if(rectA.bottom < video.bottom) {
			// elementA.style.backgroundColor = 'rgba(255, 255, 255, 0)';
		} else {
			// elementA.style.backgroundColor = 'white';
		}

        // 요소 A와 B가 만나는 순간
        if (rectA.top < rectB.bottom && rectB.top <= rectA.bottom) {
            // elementA.style.position = 'absolute'; // 요소 A를 절대 위치로 설정
            // elementA.style.top = `${rectB.top - rectA.height}px`; // 요소 B 위로 이동
            elementB.style.position = 'sticky';
            elementB.style.top = 0;
            elementB.style.zIndex = 11;
            elementB.style.backgroundColor = 'white';
            // elementB.style.fontSize = '30px';
        } else {
            elementA.style.position = 'sticky'; // 다시 sticky로 설정
            elementA.style.top = '0'; // 원래 위치로 복구
            // elementB.style.fontSize = '40px';
            elementB.style.zIndex = 10;
        }
        
        // 요소 A와 B가 만나는 순간
        if (rectA.top < rectC.bottom && rectC.top <= rectA.bottom) {
            // elementA.style.position = 'absolute'; // 요소 A를 절대 위치로 설정
            // elementA.style.top = `${rectB.top - rectA.height}px`; // 요소 B 위로 이동
            elementC.style.position = 'sticky';
            elementC.style.top = 0;
            elementC.style.zIndex = 11;
            elementC.style.backgroundColor = 'white';
            // elementC.style.fontSize = '30px';
        } else {
            elementA.style.position = 'sticky'; // 다시 sticky로 설정
            elementA.style.top = '0'; // 원래 위치로 복구
            // elementC.style.fontSize = '40px';
            elementC.style.zIndex = 10;
            elementC.style.backgroundColor = 'rgba(255, 255, 255, 0)';
        }
    });
    
    
	document.querySelectorAll('.flip-vertical-right').forEach(card => {
	    card.addEventListener('mouseenter', () => {
	        card.animate([
	            { transform: 'rotateY(0deg)' },
	            { transform: 'rotateY(180deg)' }
	        ], {
	            duration: 500,
	            fill: 'forwards' // 애니메이션 끝 상태 유지
	        });
	
	        // 뒷면 글자 보이기
	        const backFace = card.querySelector('.back-face');
	        backFace.animate([
	            { opacity: 0 },
	            { opacity: 1 }
	        ], {
	            duration: 500,
	            fill: 'forwards'
	        });
	    });
	
	    card.addEventListener('mouseleave', () => {
	        card.animate([
	            { transform: 'rotateY(180deg)' },
	            { transform: 'rotateY(0deg)' }
	        ], {
	            duration: 500,
	            fill: 'forwards' // 애니메이션 끝 상태 유지
	        });
	
	        // 뒷면 글자 숨기기
	        const backFace = card.querySelector('.back-face');
	        backFace.animate([
	            { opacity: 1 },
	            { opacity: 0 }
	        ], {
	            duration: 500,
	            fill: 'forwards'
	        });
	    });
	});

});