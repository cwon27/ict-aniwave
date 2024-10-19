<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="/WEB-INF/inc/main_header.jspf"%>

<!DOCTYPE html>
<html lang="ko">

<head>
<meta charset="UTF-8">
<title>JSP Template</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<link rel="stylesheet" href="/ckeditor/ckeditor.css"/>
<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/super-build/ckeditor.js"></script>
<script src="/ckeditor/ckeditor.js"></script>
<link href="/css/notice2.css" rel="stylesheet" type="text/css">
</link>
</head>

<body>





<div class="all">

<section class="notice_info">
    <div class="notice_top">
        <h1 class="customer">고객센터</h1>
        <div class="aaa">
            <div class="notice_tab">
                <ul class="list">
                    <li class="selected">
                        <a href="#tap1" class="btn" id="resetFilters" style="cursor:pointer;">공지사항</a>
                    </li>
                    <li class="selected">
                        <a href="#tap2" class="btn">자주 묻는 질문</a>
                    </li>
                    <li class="selected">
                        <a href="#tap3" class="btn">1:1 문의</a>
                    </li>
                    <li class="selected">
                        <a href="#tap4" class="btn">이용약관</a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</section>


    <div class="detail_layer_pop" style="display:none;">
        <div class="detail_view">
            <div class="detail_layer_close" style="cursor:pointer;">
            <img src="/img/notice/close_1.png" alt="Close">
            </div>
            <div class="detail_view_layer">
                <div class="detil_new">

                    <div class="titarea">
                        <div class="tit">
                            <span id="modalDate"></span>
                            <div id="modalTitle"></div>
                        </div>
                    </div>

                    <div class="detail_content">
                        <div class="sample_txt">
                        <div id="modalContent"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="con_width">
            <div class="notice" id="content">
                <div class="tools">
                    <div class="search">
                        <select>
                            <option>제목</option>
                        </select>
                        <input type="text" id="textSearch" value="${keyword}" placeholder="검색어를 입력하세요." />
                        <button type="button" id="btnSearch">검색</button>
                    </div>
                </div>

                <!-- <h2 class="search-tags">
                    <a class="${selectedTag == '' ? 'on' : ''}" style="cursor:pointer;" onclick="filterByTag('')">#전체</a>
                    <a class="${selectedTag == '쇼핑' ? 'on' : ''}" style="cursor:pointer;" onclick="filterByTag('쇼핑')">#쇼핑</a>
                    <a class="${selectedTag == '계정' ? 'on' : ''}" style="cursor:pointer;" onclick="filterByTag('계정')">#계정</a>
                    <a class="${selectedTag == '포인트' ? 'on' : ''}" style="cursor:pointer;" onclick="filterByTag('포인트')">#포인트</a>
                    <a class="${selectedTag == '애니' ? 'on' : ''}" style="cursor:pointer;" onclick="filterByTag('애니')">#애니</a>
                    <a class="${selectedTag == '서비스' ? 'on' : ''}" style="cursor:pointer;" onclick="filterByTag('서비스')">#서비스</a>
                </h2> -->
            </div>

            <!-- 공지사항 리스트 -->
            <div class="bbb">
                <div class="content active" id="tap1"> <!-- 기본적으로 active 클래스를 추가 -->
                    <div class="noticeList" id="noticeList">
                        <div class="row header">
                            <div class="col-sm-1 p-2">번호</div>
                            <div class="col-sm-2 p-2">카테고리</div>
                            <div class="col-sm-8 p-2">제목</div>
                            <div class="col-sm-1 p-2">등록일</div>
                        </div>

                        <!-- 공지사항 리스트 반복 출력 -->
                            <c:forEach var="notice" items="${notices}">
                                <div class="row">
                                    <div class="col-sm-1 p-2">${notice.idx}</div>
                                    <div class="col-sm-8 p-2">
                                        <a href="#" class="noticeTitle" data-title="${notice.title}" data-content="${notice.content}">
                                            ${notice.title}
                                        </a>
                                    </div>
                                    <div class="col-sm-1 p-2"></div>
                                    <div class="col-sm-2 p-2">${notice.regDT}</div>
                                </div>
                            </c:forEach>
                    </div>

                    <!-- 페이지네이션 -->
                        <div class="pagination">
                            <ul class="paging">
                                <c:if test="${paging.hasPrevious}">
                                    <li><a href="?page=${paging.nowPage - 1}&tag=${selectedTag}&keyword=${keyword}">&lt;</a></li>
                                </c:if>

                                <c:forEach var="i" begin="${paging.startPage}" end="${paging.endPage}">
                                    <li class="<c:if test='${paging.nowPage == i}'>active</c:if>">
                                        <a href="?page=${i}&tag=${selectedTag}&keyword=${keyword}">${i}</a>
                                    </li>
                                </c:forEach>

                                <c:if test="${paging.hasNext}">
                                    <li><a href="?page=${paging.nowPage + 1}&tag=${selectedTag}&keyword=${keyword}">&gt;</a></li>
                                </c:if>
                            </ul>
                        </div>

                </div>

                <!-- 자주 묻는 질문 -->
                <div class="content" id="tap2">
                    <div class="FAQList">
                        <div class="f_header" style="height:auto; overflow:hidden; text-align:center;">
                            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="customer_table2">
                                <colgroup>
                                    <col width="20">
                                    <col width="*">
                                </colgroup>
                                <tbody>
                                    <!-- FAQ 리스트 반복 출력 -->
                                    <c:forEach var="faq" items="${faqs}">
                                        <tr class="faq-question" style="cursor:pointer;">
                                            <td>Q</td>
                                            <td>
                                                ${faq.question}  <!-- 질문 내용 -->
                                                <img src="img/notice/down.png" class="toggle-icon" style="width: 16px; height: 16px; vertical-align: middle;">
                                            </td>
                                        </tr>

                                        <!-- 답변 항목 (초기에는 숨김 처리) -->
                                        <tr class="faq-answer" style="display: none;">
                                            <td colspan="2">
                                                <div class="answer-text" style="padding: 15px; background: #333; color: #ddd;">
                                                    <p>A. ${faq.answer}</p> <!-- 답변 내용 -->
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>


                <div class="content" id="tap3">
                    <div class="inquiry-container">
                        <!-- 카테고리 및 문의 영역 -->
                        <form class="inquiry-form" method="post" onsubmit = "return submitInquiry()" enctype="multipart/form-data">
                            <table class="inquiry-table">
                                <!-- 구매 관련 문의 -->
                                <tr>
                                    <th>구매 관련 문의</th>
                                    <td class="category-options">
                                        <label><input type="radio" name="qnatype" value="1"> 배송문의</label>
                                        <label><input type="radio" name="qnatype" value="2"> 주문문의</label>
                                        <label><input type="radio" name="qnatype" value="3"> 취소문의</label>
                                        <label><input type="radio" name="qnatype" value="4"> 반품문의</label>
                                        <label><input type="radio" name="qnatype" value="5"> 교환문의</label>
                                        <label><input type="radio" name="qnatype" value="6"> 환불문의</label>
                                    </td>
                                </tr>
                                <tr class="line"><td colspan="2"></td></tr>

                                <!-- 일반 상담 문의 -->
                                <tr>
                                    <th>일반 상담 문의</th>
                                    <td class="category-options">
                                        <label><input type="radio" name="qnatype" value="7"> 회원정보문의</label>
                                        <label><input type="radio" name="qnatype" value="8"> 회원제도문의</label>
                                        <label><input type="radio" name="qnatype" value="9"> 결제방법문의</label>
                                        <label><input type="radio" name="qnatype" value="10"> 상품문의</label>
                                    </td>
                                </tr>
                                <tr class="line"><td colspan="2"></td></tr>

                                <!-- 기타 문의 -->
                                <tr>
                                    <th>기타 문의</th>
                                    <td class="category-options">
                                        <label><input type="radio" name="qnatype" value="11"> 기타</label>
                                    </td>
                                </tr>
                                <tr class="line"><td colspan="2"></td></tr>

                                <!-- 아이디는 로그인된 사용자 아이디를 서버에서 불러와 표시 -->
                                <tr>
                                    <th>아이디</th>
                                    <td><span class="user-id">${userid}</span></td>
                                </tr>
                                <tr class="line"><td colspan="2"></td></tr>

                                <!-- 제목 입력 -->
                                <tr>
                                    <th>제목</th>
                                    <td><input type="text" name="title" id="title" placeholder="제목을 입력하세요." class="inquiry-title"></td>
                                </tr>
                                <tr class="line"><td colspan="2"></td></tr>

                                <!-- 내용 입력 -->
                                <tr>
                                    <th>내용</th>
                                    <td><textarea name="content" id="content" placeholder="내용을 입력하세요." class="inquiry-content"></textarea></td>
                                </tr>
                                <tr class="line"><td colspan="2"></td></tr>

                                <!-- 사진 첨부 -->
                                <tr>
                                    <th>사진 첨부</th>
                                    <td>
                                        <input type="file" name="file" id="file" class="inquiry-attachment" multiple>
                                        <p class="attachment-info">파일용량은 최대 10MB로 제한되며, 1개의 파일을 첨부할 수 있습니다.</p>
                                    </td>
                                </tr>
                            </table>

                            <!-- 등록 버튼 -->
                            <div class="inquiry-submit">
                                <button type="submit" class="submit-btn">1:1 문의하기</button>
                            </div>
                        </form>
                    </div>


                <div class="content" id="tap4">
                    <div class="terms-container">
                        <h2>서비스 이용약관</h2>
                        <div class="terms-content">
                            <p><strong>제 1 조 (목적)</strong></p>
                            <p>본 약관은 주식회사 애니웨이브(이하 "회사")가 제공하는 콘텐츠와 재화 또는 용역(이하 "상품")을 구입하는 경우(이하 "구매")와 관련하여 회사와 이용자와의 권리, 의무 및 책임사항 등을 규정함을 목적으로 합니다.</p>

                            <p><strong>제 2 조 (용어의 정의)</strong></p>
                            <p>이 약관에서 사용하는 용어의 정의는 다음과 같습니다. <br>
                            1. "회사" : 회사 또는 회원이 서비스 계약을 통해 이용 계약을 체결하여 제공받는 서비스 전반을 의미합니다.<br>
                            2. "이용자" : 회원 가입을 완료하고 회사의 서비스에 접근하여 이용하는 모든 개인 및 단체를 의미합니다.<br>
                            3. "서비스" : 회사가 제공하는 모든 온라인, 오프라인 서비스를 의미합니다.</p>


                        </div>
                    </div>
                </div>
            </div>



        </div>

    </div>
</div>


<script>
window.onload = function(){

    var token = localStorage.getItem("token"); // 토큰 값 가져오기
    if(token != "" && token != null){
        $.ajax({
            url: "/getuser",
            type: "get",
            data:{Authorization:token},
            success: function(vo){
                document.querySelector('.user-id').innerText = vo.userid;
                console.log("로그인된 사용자 ID:" + vo.userid);

                userid = vo.userid;
                useridx = vo.idx;

                console.log(userid);
                console.log(useridx);
            },
            error: function(){
                console.error("로그인 여부 확인 실패");
            }
        });
    }

    // tap3 탭 클릭 시 로그인 여부 확인
        document.querySelector('.notice_tab .list li:nth-child(3) .btn').addEventListener('click', function() {
            if (!token) { // 토큰이 없으면
                alert('로그인이 필요합니다.');
                location.href = "/user/login"; // 로그인 페이지로 이동
                return; // 이후 코드 실행 중단
            }

            // 로그인된 사용자는 tap3의 콘텐츠에 접근 가능
            console.log('1:1 문의하기 탭에 접근했습니다.');
        });
};


    // 공지사항 탭 클릭 시 필터 초기화하고 전체 목록 요청
    document.getElementById('resetFilters').addEventListener('click', function() {
        // AJAX 요청으로 필터링 없이 전체 공지사항 리스트 요청
        $.ajax({
            url: '/notice2',
            type: 'GET',
            data: { keyword: '', tag: '' },  // 검색어와 태그를 초기화
            success: function (data) {
                // #noticeList 부분 업데이트
                const newNoticeList = $(data).find('#noticeList').html();
                document.getElementById('noticeList').innerHTML = newNoticeList;

                // 검색어 입력 필드 초기화
                document.getElementById('textSearch').value = '';
            },
            error: function (error) {
                console.log('Error:', error);
            }
        });
    });


    // 검색 실행 함수
    function triggerSearch() {
        const keyword = document.getElementById('textSearch').value;

        $.ajax({
            url: '/notice2',
            type: 'GET',
            data: { keyword },
            success: function (data) {
                document.getElementById('noticeList').innerHTML = $(data).find('#noticeList').html();
                document.getElementById('textSearch').value = '';  // 검색어 입력 필드 초기화
            },
            error: function (error) {
                console.log('Error:', error);
            }
        });
    }

    // 검색바 표시/숨기기 함수
    function toggleSearchBar(show) {
        document.querySelector('.search').style.display = show ? 'block' : 'none';
    }

    // 탭 전환 시 검색바 처리
    document.querySelectorAll('.notice_tab .list li').forEach(tab => {
        tab.addEventListener('click', function() {
            const isNoticeTab = this.querySelector('.btn').innerText === '공지사항';
            toggleSearchBar(isNoticeTab);  // 공지사항 탭이면 검색바 표시, 아니면 숨김
        });
    });

    // 검색 버튼 클릭 또는 엔터 키 입력 시 검색 실행
    ['click', 'keypress'].forEach(eventType => {
        document.getElementById('textSearch').addEventListener(eventType, function(event) {
            if (eventType === 'click' || (eventType === 'keypress' && event.key === 'Enter')) {
                event.preventDefault();
                triggerSearch();
            }
        });
    });

    // 페이지 로드 시 검색바 고정 (공지사항 탭에서 시작할 경우)
    toggleSearchBar(true);  // 페이지 로드 시 항상 공지사항에는 검색바가 표시



    // 모든 질문 항목에 대해 클릭 이벤트 추가
        document.querySelectorAll('.faq-question').forEach(question => {
            question.addEventListener('click', function () {
                // 현재 질문의 다음 요소(답변)를 가져옴
                const answer = this.nextElementSibling;

                // 모든 답변을 숨기고 아이콘을 초기화
                document.querySelectorAll('.faq-answer').forEach(ans => {
                    if (ans !== answer) ans.style.display = 'none';
                });

                document.querySelectorAll('.toggle-icon').forEach(icon => {
                    icon.src = "img/notice/down.png"; // 모든 아이콘을 down으로 초기화
                });

                // 현재 답변을 보이거나 숨기기
                answer.style.display = (answer.style.display === 'none' || answer.style.display === '') ? 'table-row' : 'none';

                // 아이콘 변경
                const icon = this.querySelector('.toggle-icon');
                icon.src = (answer.style.display === 'table-row') ? "img/notice/up.png" : "img/notice/down.png";
            });
        });





//탭1_공지사항_모달
function setNoticeTitleClickEvents() {
    document.querySelectorAll('.noticeTitle').forEach(item => {
        item.addEventListener('click', function (e) {
            e.preventDefault(); // 링크 기본 동작 방지

            // 모달 내용 업데이트
            const title = this.getAttribute('data-title');
            const content = this.getAttribute('data-content');

            document.getElementById('modalTitle').innerHTML = title;
            document.getElementById('modalContent').innerHTML = content;
            document.querySelector('.detail_layer_pop').style.display = 'block'; // 모달 보이기
        });
    });
}

// 모달 닫기 이벤트 설정
document.querySelector('.detail_layer_close').addEventListener('click', function () {
    document.querySelector('.detail_layer_pop').style.display = 'none'; // 모달 숨기기
});

// 초기 로드 시 공지사항의 모달 이벤트 설정
setNoticeTitleClickEvents();

// 탭 전환 시마다 이벤트 리스너 재설정 및 컨텐츠 갱신
const tabList = document.querySelectorAll('.notice_tab .list li');
const contentList = document.querySelectorAll('.content');

tabList.forEach((tab, index) => {
    tab.querySelector('.btn').addEventListener('click', function (e) {
        e.preventDefault();
        // 모든 탭 비활성화 및 콘텐츠 숨김
        tabList.forEach(t => t.classList.remove('selected'));
        contentList.forEach(content => content.classList.remove('active'));

        // 현재 탭 활성화 및 해당 콘텐츠 보여줌
        tab.classList.add('selected');
        contentList[index].classList.add('active');

        // 공지사항 탭으로 돌아왔을 때 이벤트 리스너 재설정
        if (index === 0) { // 공지사항 탭인 경우
            setNoticeTitleClickEvents();
        }
    });
});

// 공지사항 리스트가 AJAX로 새로 로드될 때마다 이벤트 리스너 재설정
$(document).ajaxComplete(function() {
    setNoticeTitleClickEvents();
});



// 1:1 문의 제출 함수
function submitInquiry() {
    //let formData = new FormData(document.querySelector(".inquiry-form"));
    let formData = new FormData();

    // 입력 필드 값 가져오기
    let qnatype = document.querySelector('input[name="qnatype"]:checked').value;
    let title = document.querySelector('.inquiry-title').value;
    let content = document.querySelector('.inquiry-content').value;
    let files = document.querySelector('.inquiry-attachment').files;
 console.log("1", Array.from(formData.entries()));
    // FormData에 입력 값 추가
    formData.append("qnatype", qnatype);
    formData.append("title", title);
    formData.append("content", content);

    // 파일 추가
    for (let i = 0; i < files.length; i++) {
        formData.append('file', files[i]);
    }

    // 로컬 스토리지에서 JWT 토큰 가져오기
    const token = localStorage.getItem("token");
    console.log(token);
    if (!token) {
        alert('로그인이 필요합니다.');
        location.href = "/user/login";
        return false;
    }

    // 서버로 전송할 데이터를 FormData 객체에 추가
    // var formData = new FormData();
    //formData.append("file", file);

    console.log("2", Array.from(formData.entries()));
    $.ajax({
        url: '/inquirySubmit',
        type: 'POST',
        data: formData,
        processData: false, // 데이터를 기본 문자열로 처리하지 않음
        contentType: false, // "multipart/form-data"로 전송
        headers: {
                "Authorization": "Bearer "+token   // JWT 토큰을 Authorization 헤더에 포함
          },
        success: function(response) {
            alert('1:1 문의가 등록되었습니다.');
            location.reload(); // 성공 시 페이지 새로고침
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                alert('인증에 실패했습니다. 다시 로그인하세요.');
                location.href = "/user/login";  // 로그인 페이지로 이동
            } else if (xhr.status === 404) {
                alert('서버에서 요청을 찾을 수 없습니다.');
            } else {
                alert("요청 처리 중 오류가 발생했습니다.");
            }
            console.error("Error:", xhr);  // 오류 출력
        }
    });

    return false;  // 기본 폼 제출 방지
}





</script>

</body>

</html>