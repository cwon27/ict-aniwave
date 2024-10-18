

// URL에서 특정 파라미터의 값을 가져오는 함수
function getParameterByName(name) {
    let url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    let regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}



let pageNum = getParameterByName('pageNum') || 1; // URL에서 pageNum 추출, 기본값 1
let pageSize = getParameterByName('pageSize') || 10; // URL에서 pageSize 추출, 기본값 10
let category = getParameterByName('category');  // category 추가
let secondCategory = getParameterByName('second_category'); // second_category 추가

// 필터링 조건이 있을 경우 파라미터에 추가
let url = `/pagedProducts?pageNum=${pageNum}&pageSize=${pageSize}`;
if (category) {
    url += `&category=${category}`;
}
if (secondCategory) {
    url += `&second_category=${secondCategory}`;
}

fetch('/pagedProducts?pageNum=' + pageNum + '&pageSize=' + pageSize)
    .then(response => response.json())
    .then(data => {
        console.log(data);  // 데이터가 제대로 오는지 확인
        // 데이터 처리 로직 추가
    })
    .catch(error => console.error('Error:', error));

document.addEventListener('DOMContentLoaded', function() {
});



//필터에 따른 상품 리스트 불러오기
function loadSubcategories(categoryCode) {
    const categoryParam = categoryCode ? `&category=${categoryCode}` : '';
    const pageNum = 1;  // 페이지 넘버, 예시로 1로 고정
    const pageSize = 10;  // 페이지 크기, 예시로 10으로 고정
    $.ajax({
        url: `/pagedProducts?pageNum=${pageNum}&pageSize=${pageSize}${categoryParam}`,
        method: 'GET',
        data: { code: categoryCode },
        success: function(data) {  // 여기서 data로 변경
            const subcategoryList = document.getElementById('subcategory-list');
            subcategoryList.innerHTML = '';  // 기존 하위 카테고리 목록 초기화

            // 하위 카테고리 목록 생성 및 이벤트 리스너 추가
            data.forEach(subcategory => {  // 여기서도 subcategories 대신 data로 접근
                const listItem = document.createElement('li');
                listItem.className = 'filter-item';
                listItem.innerHTML = `<span class="filter-text">${subcategory}</span>`;
                listItem.addEventListener('click', function() {
                    // 다른 모든 아이템에서 active 제거
                    document.querySelectorAll('.filter-item').forEach(item => item.classList.remove('active'));
                    // 클릭한 아이템에 active 클래스 추가
                    listItem.classList.add('active');
                    // 필터 적용
                    filterProductsByServer();
                });
                subcategoryList.appendChild(listItem);
            });
        },
        error: function(error) {
            console.error('Error loading subcategories:', error);
        }
    });
}

// 필터를 적용하는 함수
function applyFilter(category, second_category) {
    const pageNum = 1;
    const pageSize = 10;

    // AJAX 요청에서 category와 secondCategory를 서버로 전달
    $.ajax({
        url: `/pagedProducts?pageNum=${pageNum}&pageSize=${pageSize}`,
        method: 'GET',
        data: {
            category: category,  // 선택된 카테고리 전달
            second_category: second_category || null // second_category가 있으면 전달, 없으면 null
        },
        success: function(data) {
            // 성공 시 처리
            console.log("필터링된 상품 목록: ", data);
            // 필터링된 상품 목록을 화면에 표시하는 로직 추가
            updateProductList(data);
        },
        error: function(error) {
            console.error("필터 적용 중 오류 발생: ", error);
        }
    });
}

let productList;
let products;
document.addEventListener('DOMContentLoaded', function() {
    productList = document.querySelector('.list-carousel-images');
    if (!productList) {
        console.error("상품 목록 컨테이너를 찾을 수 없습니다.");
        return;
    }

    // 서버에서 상품 목록을 가져오는 API 호출
    fetch('/pagedProducts?pageNum=1&pageSize=10')
        .then(response => response.json())  // 응답을 JSON으로 변환
        .then(data => {
            products = data;  // 전역 변수 products에 데이터 할당
            // 서버에서 받은 데이터를 가지고 productList를 업데이트
            data.forEach(product => {
                const listItem = document.createElement('li');
                listItem.className = 'list-product';
                listItem.setAttribute('data-date', product.date);
                listItem.setAttribute('data-popular', product.popularity);  // 좋아요 수 반영
                listItem.setAttribute('data-price', product.price);

                listItem.innerHTML = `
                    <a href="/storeDetail/${product.idx}">
                        <img src="http://192.168.1.92:8000/${product.thumImg}" alt="${product.title}">
                    </a>
                    <p>${product.title}</p>
                    <p>${product.price.toLocaleString()} 원</p>
                `;
                productList.appendChild(listItem);
            });

            // 기본 필터 적용 (예: 최신순)
            filterProductsByType('latest');
        })
        .catch(error => {
            console.error('상품 목록을 가져오는 중 오류가 발생했습니다:', error);
        });


});
// 필터 적용 함수 정의
function filterProductsByType(filterType) {
console.log("filterProductsByType->",filterType)

    // 필터 타입에 따른 정렬을 시도하기 전에 products가 배열인지 확인
    if (!Array.isArray(products)) {
        console.error("products가 배열이 아니거나 정의되지 않았습니다.");
        return;
    }

    // 모든 필터 버튼에서 'active' 클래스 제거
    const allFilters = document.querySelectorAll('.filter-options');
    allFilters.forEach(filter => filter.classList.remove('active'));

    // 클릭된 필터 버튼에 'active' 클래스 추가
    const activeFilter = document.querySelector(`.filter-options[onclick="filterProductsByType('${filterType}')"]`);
    if (activeFilter) {
        activeFilter.classList.add('active');
    }


    console.log(products);
    // 필터 타입에 따른 정렬
    if (filterType === 'latest') {
        products.sort((a, b) => new Date(b.regDT) - new Date(a.regDT));  // 최신순 정렬
    } else if (filterType === 'popular') {
        products.sort((a, b) => parseInt(b.likeCount) - parseInt(a.likeCount));  // 인기순 (좋아요 수) 정렬
    } else if (filterType === 'high-price') {
        products.sort((a, b) => parseInt(b.price) - parseInt(a.price));  // 높은 가격순 정렬
    } else if (filterType === 'low-price') {
        products.sort((a, b) => parseInt(a.price) - parseInt(b.price));  // 낮은 가격순 정렬
    }


    updateProductList(products);
}
//상품검색창
function searchProducts() {
    const input = document.getElementById('productSearch').value.toLowerCase();
    const productItems = document.querySelectorAll('.list-product'); // 상품 리스트

    productItems.forEach(item => {
        const productName = item.textContent.toLowerCase();
        if (productName.includes(input)) {
            item.style.display = ''; // 검색어가 포함된 항목 보이기
        } else {
            item.style.display = 'none'; // 검색어가 포함되지 않은 항목 숨기기
        }
    });
}

function updateProductList(products) {
    console.log("updateProductList(products)=>",products);
    productList = document.querySelector('.list-carousel-images');
    console.log("line193");  // productList가 null이 아닌지 확인
    if (!productList) {
        console.error("상품 목록 컨테이너를 찾을 수 없습니다.");
        return;  // productList가 정의되지 않았을 경우 함수 종료
    }


    productList.innerHTML = '';  // 기존의 상품 목록을 지움


    products.forEach(product => {
        const listItem = document.createElement('li');
        listItem.className = 'list-product';
        listItem.innerHTML = `
            <a href="/storeDetail/${product.idx}">
                <img src="http://192.168.1.92:8000/${product.thumImg}" alt="${product.title}">
            </a>
            <p>${product.title}</p>
            <p>${product.price.toLocaleString()} 원</p>
        `;
        productList.appendChild(listItem);  // 새로운 상품 목록을 추가
    });
}

////////////////////////////////////////

  const subcategoryMap = {
      '아우터': 10,
      '상의': 11,
      '하의': 12,
      '잡화': 13,
      '아크릴': 20,
      '피규어': 21,
      '캔뱃지': 22,
      '슬로건': 23,
      '포스터': 24,
      '기타': 25,
      '필기류': 30,
      '노트&메모지': 31,
      '파일': 32,
      '스티커': 33,
      '달력': 34,
      '기타': 35,
      '컵&텀블러': 40,
      '쿠션': 41,
      '담요': 42,
      '기타': 43
  };

  window.applyFilter = function(category, second_category) {
      console.log("선택된 카테고리: ", category);
      console.log("선택된 하위 카테고리: ", second_category);
      const pageNum = 1;
      const pageSize = 10;

      // second_category 값을 subcategoryMap에서 가져옴
      const secondCategoryValue = subcategoryMap[second_category] || null;

      // category 값이 없을 때는 파라미터에서 제외
      const requestData = {
          pageNum: pageNum,
          pageSize: pageSize,
          second_category: secondCategoryValue
      };

      if (category) {
          requestData.category = category;
      }

      // AJAX 요청에서 category와 secondCategory를 서버로 전달
      $.ajax({
          url: `/pagedProducts`,
          method: 'GET',
          data: requestData,
          success: function(data) {
              console.log("필터링된 상품 목록: ", data);
              products = data;
              updateProductList(data); // 필터링된 상품 목록을 화면에 업데이트하는 함수
          },
          error: function(error) {
              console.error("필터 적용 중 오류 발생: ", error);
              console.log("전달된 데이터:", requestData);
          }
      });
  };


