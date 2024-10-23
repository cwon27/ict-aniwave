<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="/WEB-INF/inc/page_header.jspf"%>

<link rel="stylesheet" href="/css/mypage.css" type="text/css" />
<link rel="stylesheet" href="/css/mypage_comm.css" type="text/css" />
<script src="/js/mypage_common.js"></script>
<script src="/js/mypage_comm.js"></script>

<div class="mypage_wrap">
  <div class="mypage_main_wrap">
    <div class="mypage_left">
      <a href="/user/mypage" class="mypage_home">MY PAGE</a>
      <section class="mypage_menu">
          <h3>나의 쇼핑정보</h3>
          <ul>
            <li>
              <a href="/user/mypage_order">주문배송조회</a>
            </li>
            <li>
              <a href="/user/mypage_review"> 상품 리뷰 </a>
            </li>
          </ul>
          <h3>나의 계정설정</h3>
          <ul>
            <li>
              <a href="/user/mypage_userEdit">회원정보수정</a>
            </li>
            <li>
              <a href="/user/mypage_comm" class="option_active">내가 쓴 글</a>
            </li>
            <li>
              <a href="/user/mypage_point">적립금</a>
            </li>
            <li>
              <a href="/user/mypage_heart">좋아요</a>
            </li>
          </ul>
          <h3>고객센터</h3>
          <ul>
            <li>
              <a href="/user/mypage_qna">1:1 문의</a>
            </li>
            <li>
              <a href="">공지사항</a>
            </li>
            <li>
              <a href="">FAQ</a>
            </li>
          </ul>
        </section>

      <section>
        <div class="btn_bx">
          <button type="button" class="logout_btn" onclick="logout()">LOGOUT</button>
        </div>
      </section>
    </div>
    <div class="mypage_right">
      <ul class="mypage_right_top">
          <li class="user_inform">
            <a class="atag_css" href="/user/mypage_userEdit"
              ><strong>회원정보 수정</strong><span class="currentID"></span></a
            >
          </li>
          <li class="user_retention_details">
            <a class="atag_css" href="/user/mypage_point"
              ><strong class="">적립금</strong><span class="reservePoint"></span></a
            >
          </li>
          <li class="user_retention_details">
            <a class="atag_css" href="/user/mypage_review"
              ><strong class="">작성한 리뷰</strong><span class="afterCountSpan"></span></a
            >
          </li>
        </ul>

      <div class="mypage_right_element">
        <!-- 여기에 페이지에 맞는 요소 넣으면 됨 -->
        <h4 class="my_tit_cm">나만의 커뮤니티</h4>
        <div class="cm_top">
          <div>
            <span
              >부적절한 글로 인한 신고 누적 시 계정 이용에 제한이 발생할 수
              있습니다.</span
            >
            <span
              >반복적인 위반 시 영구 이용 정지 조치가 이루어질 수
              있습니다.</span
            >
          </div>
          <div>
            <button type="button" class="cm_write_btn" onclick="location.href='/cmWrite'">
              커뮤니티 글쓰기
            </button>
          </div>
        </div>

        <div class="mycm_list">
          <ul>
            <li class="mycm_list_li">
              <div class="cm_type">
                <span>카테고리</span>
              </div>
              <div class="cm_title">
                <span>제목</span>
              </div>
              <div class="cm_regDT">
                <span>작성일</span>
              </div>
              <div class="cm_replyState">
                <span>조회수</span>
              </div>
              <div class="cm_delBtn_div"></div>
            </li>
            <div>
              <ul class="mycm_write_list_ul">

              </ul>
            </div>
          </ul>
        </div>
        <div id="pagination">
          <div class="custom-pagination"></div>
        </div>
      </div>
    </div>
  </div>
</div>

