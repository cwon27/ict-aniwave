<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="/WEB-INF/inc/Masterheader.jspf" %>
<title>DashBoard - 게시판 전체 목록</title>
<link href="/css/masterStyle.css" rel="stylesheet" type="text/css"></link>
<div class="boardManagement">
            <h2>게시판 전체 목록</h2>
            <div class="summary">
                        <div>
                            <strong>총 게시글 수</strong>
                            <p id="totalBoard">${totalUser} 명</p>
                        </div>
                        <div>
                            <strong>오늘 작성된 게시글 수 </strong>
                            <p id="newUsers">${newUsers}  명</p>
                        </div>
                        <div>
                            <strong>7일간 작성된 게시글 수</strong>
                            <p id="newSignups">${newSignups}  명</p>
                        </div>
                    </div>
            <table class="table table-hover table-bordered">
                <thead class="table-light">
                    <tr>
                        <th style="width:2%">No.</th>
                        <th style="width:6%">카테고리</th>
                        <th style="width:30%">제목</th>
                        <th style="width:5%">작성자</th>
                        <th style="width:7%">작성일</th>
                        <th style="width:4%">조회수</th>
                        <th style="width:10%">관리</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="board" items="${boardList}">
                    <tr>
                        <td>${board.idx}</td>
                        <td>${board.category}</td>
                        <td>${board.title}</td>
                        <td>${board.author}</td>
                        <td>${board.regDT}</td>
                        <td>${board.hit}</td>
                        <td>
                            <a href="/cmView/${board.idx}" class="btn btn-outline-success btn-sm">상세보기</a>
                            <a href="#" class="btn btn-outline-danger btn-sm" onclick="return confirm('정말 삭제하시겠습니까?');">삭제</a>
                        </td>
                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- 페이징 영역 -->
        <nav>
           <ul class="pagination justify-content-center">
               <c:set var="pageGroupSize" value="10" />
               <c:set var="startPage" value="${((currentPage - 1) / pageGroupSize) * pageGroupSize + 1}" />
               <c:set var="endPage" value="${startPage + pageGroupSize - 1 > totalPages ? totalPages : startPage + pageGroupSize - 1}" />

               <!-- 이전 그룹으로 이동 -->
               <c:if test="${startPage > 1}">
                   <li class="page-item">
                       <a class="page-link" href="/master/storeMasterList?currentPage=${startPage - 1}&pageSize=${pageSize}">이전</a>
                   </li>
               </c:if>

               <!-- 페이지 번호 -->
               <c:forEach var="i" begin="${startPage}" end="${endPage}">
                   <li class="page-item ${i == currentPage ? 'active' : ''}">
                       <a class="page-link" href="/master/storeMasterList?currentPage=${i}&pageSize=${pageSize}">${i}</a>
                   </li>
               </c:forEach>

               <!-- 다음 그룹으로 이동 -->
               <c:if test="${endPage < totalPages}">
                   <li class="page-item">
                       <a class="page-link" href="/master/storeMasterList?currentPage=${endPage + 1}&pageSize=${pageSize}">다음</a>
                   </li>
               </c:if>
           </ul>
        </nav>

