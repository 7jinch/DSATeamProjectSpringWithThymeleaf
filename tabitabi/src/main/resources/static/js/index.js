document.addEventListener('DOMContentLoaded', function() {
	let currentIndex = 0;
	const slides = document.querySelectorAll('.slide');
	const totalSlides = slides.length;

	updateSlidePosition(); // 페이지 로드 시 첫 번째 슬라이드 보이기

	document.querySelector('.next').addEventListener('click', () => {
		if (currentIndex < totalSlides - 2) { // 두 개의 이미지를 보여주기 때문에 -2
			currentIndex++;
			updateSlidePosition();
		}
	});

	document.querySelector('.prev').addEventListener('click', () => {
		if (currentIndex > 0) {
			currentIndex--;
			updateSlidePosition();
		}
	});

	function updateSlidePosition() {
		const offset = currentIndex * -50; // 현재 인덱스에 따라 슬라이드를 왼쪽으로 이동
		slides.forEach((slide, index) => {
			slide.style.display = 'block'; // 모든 슬라이드를 보이게 함
		});
		document.querySelector('.slider-container').style.transform = `translateX(${offset}%)`;
	}
	// 카테고리 드롭다운 메뉴 보이기/숨기기
	const categoryButton = document.getElementById('category-button');
	const categoryMenu = document.getElementById('category-menu');

	categoryButton.addEventListener('click', function() {
		categoryMenu.classList.toggle('show');
	});

	const products = document.querySelector('.products');
	const prevBtn = document.querySelector('.prev-btn');
	const nextBtn = document.querySelector('.next-btn');

	let currentPosition = 0;
	const itemWidth = document.querySelector('.product-item').offsetWidth;
	const visibleItems = 5; // 화면에 보이는 상품 개수
	const totalItems = document.querySelectorAll('.product-item').length;

	prevBtn.addEventListener('click', () => {
		if (currentPosition > 0) {
			currentPosition--;
			updateCarousel();
		}
	});

	nextBtn.addEventListener('click', () => {
		if (currentPosition < totalItems - visibleItems) {
			currentPosition++;
			updateCarousel();
		}
	});

	function updateCarousel() {
		products.style.transform = `translateX(-${currentPosition * itemWidth}px)`;
	}


});

document.addEventListener('DOMContentLoaded', function() {
	//소매가 그래프
	fetch('/api/statistics')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(data => {
			const itemsPerPage = 4; // 페이지당 그래프 수
			let currentPage = 1;

			// 데이터를 item별로 그룹화
			const groupedData = data.reduce((acc, item) => {
				const { item: itemName, type, unit, date, averagePrice, maxPrice, minPrice } = item;
				const key = itemName; // 데이터를 item별로 그룹화
				if (!acc[key]) {
					acc[key] = {};
				}
				if (!acc[key][type]) {
					acc[key][type] = { labels: [], averagePrices: [], maxPrices: [], minPrices: [], type, unit };
				}
				acc[key][type].labels.push(date); // X축에 날짜 추가
				acc[key][type].averagePrices.push(averagePrice);
				acc[key][type].maxPrices.push(maxPrice);
				acc[key][type].minPrices.push(minPrice);
				return acc;
			}, {});

			// 각 item에 대해 차트를 생성
			const chartContainer = document.getElementById('charts-container');
			let chartItems = [];

			for (const [itemName, types] of Object.entries(groupedData)) {
				for (const [type, data] of Object.entries(types)) {
					// 각 차트를 위한 div 생성
					const chartDiv = document.createElement('div');
					chartDiv.className = 'chart-container';

					// 제목 추가 (item)
					const title = document.createElement('h4');
					title.innerText = itemName; // item명을 제목으로 표시
					title.style.textAlign = 'center'; // 제목을 중간으로 정렬
					chartDiv.appendChild(title);

					// type (unit) 추가
					const typeUnitText = document.createElement('p');
					typeUnitText.innerText = `${type} (${data.unit})`;
					typeUnitText.style.fontSize = 'small'; // 작은 글씨로 표시
					typeUnitText.style.textAlign = 'right'; // 오른쪽 정렬
					chartDiv.appendChild(typeUnitText);

					const canvas = document.createElement('canvas');
					canvas.id = `chart-${itemName}-${type}`.replace(/\s+/g, '-'); // 고유한 ID 생성
					chartDiv.appendChild(canvas);
					chartContainer.appendChild(chartDiv);

					// 차트 생성
					const ctx = canvas.getContext('2d');
					new Chart(ctx, {
						type: 'line',
						data: {
							labels: data.labels,
							datasets: [
								{
									label: `평균 가격`,
									data: data.averagePrices,
									borderColor: '#8BC34A', // 초록색 (평균 가격)
									backgroundColor: 'rgba(139, 195, 74, 0.2)',
									fill: false,
									spanGaps: false, // 데이터가 없는 부분은 건너뜀
								},
								{
									label: `최대 가격`,
									data: data.maxPrices,
									borderColor: '#FF9800', // 주황색 (최대 가격)
									backgroundColor: 'rgba(255, 152, 0, 0.2)',
									fill: false,
									spanGaps: false, // 데이터가 없는 부분은 건너뜀
								},
								{
									label: `최저 가격`,
									data: data.minPrices,
									borderColor: '#795548', // 갈색 (최저 가격)
									backgroundColor: 'rgba(121, 85, 72, 0.2)',
									fill: false,
									spanGaps: false, // 데이터가 없는 부분은 건너뜀
								},
							],
						},
						options: {
							responsive: true,
							scales: {
								x: {
									type: 'category',
									ticks: {
										maxTicksLimit: 5, // X축을 최대 5칸으로 고정
										autoSkip: true,
									},
								},
								y: {
									beginAtZero: true,
								},
							},
							plugins: {
								legend: {
									display: false, // 라벨을 표시하지 않음
								},
								tooltip: {
									enabled: true,
								},
							},
						},
					});

					// 오른쪽에 작은 글씨로 레전드 추가
					const legendContainer = document.createElement('div');
					legendContainer.style.fontSize = 'small'; // 작은 글씨
					legendContainer.style.textAlign = 'right'; // 오른쪽 정렬
					legendContainer.innerHTML = `
	                        <span style="color: #8BC34A;">■ 평균 가격</span><br>
	                        <span style="color: #FF9800;">■ 최대 가격</span><br>
	                        <span style="color: #795548;">■ 최저 가격</span>
	                    `;
					chartDiv.appendChild(legendContainer);

					chartItems.push(chartDiv);
				}
			}

			// 차트들을 랜덤으로 섞음
			chartItems = chartItems.sort(() => Math.random() - 0.5);

			// 페이지 나누기
			function updatePage() {
				chartItems.forEach((item, index) => {
					item.style.display = (index >= (currentPage - 1) * itemsPerPage && index < currentPage * itemsPerPage) ? 'block' : 'none';
				});
			}

			// 이전 버튼 클릭 핸들러
			document.getElementById('prev-btn').addEventListener('click', () => {
				if (currentPage > 1) {
					currentPage--;
					updatePage();
				}
			});

			// 다음 버튼 클릭 핸들러
			document.getElementById('next-btn').addEventListener('click', () => {
				if (currentPage * itemsPerPage < chartItems.length) {
					currentPage++;
					updatePage();
				}
			});

			// 5초마다 자동으로 다음 페이지로 넘기기
			setInterval(() => {
				if (currentPage * itemsPerPage < chartItems.length) {
					currentPage++;
				} else {
					currentPage = 1; // 마지막 페이지까지 가면 처음으로 돌아감
				}
				updatePage();
			}, 5000); // 5초마다 페이지 넘기기

			// 초기 페이지 업데이트
			updatePage();
		});
});

document.addEventListener('DOMContentLoaded', function() {
	//top10
	const products = document.querySelectorAll('.product');
	const itemsPerPage = 5;
	let currentPage = 1;

	function showPage(page) {
		// 전체 상품을 숨기기
		products.forEach((product, index) => {
			product.style.display = 'none';
		});

		// 현재 페이지의 상품만 표시
		const start = (page - 1) * itemsPerPage;
		const end = start + itemsPerPage;

		for (let i = start; i < end && i < products.length; i++) {
			products[i].style.display = 'block';
		}
	}

	// "다음" 버튼을 클릭했을 때 동작
	document.querySelector('#next-btn').addEventListener('click', function() {
		const totalPages = Math.ceil(products.length / itemsPerPage);

		if (currentPage < totalPages) {
			currentPage++;
			showPage(currentPage);
		}
	});

	// 첫 페이지를 먼저 보여줌
	showPage(currentPage);
});

document.addEventListener('DOMContentLoaded', function() {
	const products = document.querySelectorAll('.product');
	const itemsPerPage = 5;
	let currentPage = 1;

	function showPage(page) {
		// 전체 상품을 숨기기
		products.forEach((product, index) => {
			product.style.display = 'none';
		});

		// 현재 페이지의 상품만 표시
		const start = (page - 1) * itemsPerPage;
		const end = start + itemsPerPage;

		for (let i = start; i < end && i < products.length; i++) {
			products[i].style.display = 'block';
		}

		// 버튼 상태 업데이트
		document.querySelector('#prev-btn').disabled = (page === 1);
		const totalPages = Math.ceil(products.length / itemsPerPage);
		document.querySelector('#next-btn').disabled = (page === totalPages);
	}

	// "다음" 버튼을 클릭했을 때 동작
	document.querySelector('#next-btn').addEventListener('click', function() {
		const totalPages = Math.ceil(products.length / itemsPerPage);

		if (currentPage < totalPages) {
			currentPage++;
			showPage(currentPage);
		}
	});

	// "이전" 버튼을 클릭했을 때 동작
	document.querySelector('#prev-btn').addEventListener('click', function() {
		if (currentPage > 1) {
			currentPage--;
			showPage(currentPage);
		}
	});

	// 첫 페이지를 먼저 보여줌
	showPage(currentPage);
});

function openLoginPopup() {
    var width = 400;
    var height = 500;
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    window.open('/member/login', 'loginpopup', 'width=' + width + ', height=' + height + ', top=' + top + ', left=' + left);
}

