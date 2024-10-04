function filterProducts(filterType) {
    const allFilters = document.querySelectorAll('.filter-option');
    allFilters.forEach(filter => filter.classList.remove('active'));
    document.querySelector(`.filter-option[onclick="filterProducts('${filterType}')"]`).classList.add('active');

    let products = Array.from(document.querySelectorAll('.list-product'));

}
// 스크롤 이벤트 리스너
window.addEventListener('scroll', () => {
    // 스크롤 위치가 100px 이상일 때 위로 가기 버튼을 보이게 함
    if (
      document.body.scrollTop > 20 ||
      document.documentElement.scrollTop > 20
    ) {
      document.getElementById('btn-back-to-top').style.display = 'block';
    } else {
      document.getElementById('btn-back-to-top').style.display = 'none';
    }
  });
  
  // 클릭 시 페이지 맨 위로 스크롤 (애니메이션 효과 추가)
  function backToTop() {
    window.scrollTo({
      top: 0,
      behavior: 'smooth' // 부드러운 스크롤
    });
  }

  //검색창

  // 간단한 검색 기능 구현
function searchProducts() {
    const input = document.getElementById('productSearch').value.toLowerCase();
    const productItems = document.querySelectorAll('.list-product'); // 상품 리스트

    productItems.forEach(item => {
        const productName = item.innerText.toLowerCase();
        if (productName.includes(input)) {
            item.style.display = ''; // 검색어가 포함된 항목 보이기
        } else {
            item.style.display = 'none'; // 검색어가 포함되지 않은 항목 숨기기
        }
    });
}