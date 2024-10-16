package com.ict.finalproject.controller;


import com.ict.finalproject.DAO.TAdminDAO;
import com.ict.finalproject.JWT.JWTUtil;
import com.ict.finalproject.Service.MasterService;
import com.ict.finalproject.Service.MemberService;
import com.ict.finalproject.Service.TAdminService;
import com.ict.finalproject.vo.MasterVO;
import com.ict.finalproject.vo.MemberVO;
import com.ict.finalproject.vo.StoreVO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/master")
public class masterController {

    @Autowired
    TAdminService tAdminService;
    @Autowired
    MemberService service;
    @Autowired
    MasterService masterService;
    @Autowired
    JWTUtil jwtUtil;
    @Autowired
    TAdminDAO dao;
    ModelAndView mav = null;
        /*TAdminService tAdminService;


    @Autowired
        public masterController(TAdminService tAdminService, JWTUtil jwtUtil) {
        this.tAdminService = tAdminService;
        this.jwtUtil = jwtUtil;
    }*/

    // t_admin에  admin아이디 있는지 체크 하는 API
        /*@GetMapping("/checkAdmin")
        public ResponseEntity<Boolean> checkAdmin(@RequestHeader("Authorization") String authHeader) {
            // Authorization 헤더 확인
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }

            String token = authHeader.substring(7);  // 'Bearer ' 제거

            // JWT 토큰 유효성 검사
            if (!jwtUtil.validateToken(token)) {
                System.out.println("JWT 토큰이 유효하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }

            // 토큰에서 사용자 ID 추출
            String userId = jwtUtil.getUserIdFromToken(token);
            System.out.println("JWT 토큰에서 추출한 사용자 ID: " + userId);  // 추출된 ID 출력

            // t_admin 테이블의 "admin" 아이디만 접근 허용
            boolean isAdmin = tAdminService.existsByAdminId(userId);
            System.out.println("t_admin 테이블에 " + userId + " 존재 여부: " + isAdmin);

            return ResponseEntity.ok(isAdmin);  // 관리자 여부를 반환
        }*/

    @ModelAttribute("unansweredCount")
    public int unansweredQnaCount() {
        return masterService.getUnansweredQnaCount();  // 미답변 문의 수 조회
    }

    // 관리자페이지 로그인 매핑
    @GetMapping("/admin_login")
    public String adminLogin() {
        return "join/admin_login";
    }


    // Dashboard 매핑
    @GetMapping("/masterMain")
    public ModelAndView masterMain() {
        // 문의 사항 테이블에서 답변 안된 문의 개수 카운트
        int unanswerCount = masterService.getUnansweredQnaCount();
        System.out.println("hey! 모두들 안녕 내가 누군지 아니?");
        mav = new ModelAndView();
        mav.addObject("unanswerCount", unanswerCount);
        mav.setViewName("master/masterMain");  // 뷰 이름 설정
        return mav;  // 중복 리다이렉트 발생 여부 확인
    }

    //Dashboard - 회원관리 - 회원 목록 리스트
    @GetMapping("/userMasterList")
    public ModelAndView masterUserList(MemberVO vo) {

        // 유저 List 가져오기
        List<MemberVO> memberList = service.getMemberList(vo);

        // 총 유저수 구하기
        int totalUser = service.getTotalUser();

        // 오늘 가입자 수 구하기
        int newUsers = service.getNewUsers();

        // 최근 7일간 가입자 수 구하기
        int newSignups = service.getNewSignups();

        mav = new ModelAndView();
        mav.addObject("memberList", memberList);
        mav.addObject("totalUser", totalUser);
        mav.addObject("newUsers", newUsers);
        mav.addObject("newSignups", newSignups);
        mav.setViewName("master/userMasterList");
        return mav;
    }

    @GetMapping("/userDelMasterList")
    public ModelAndView masterUserDelList(MasterVO vo) {
        List<MasterVO> memberDelList = masterService.getMemberDelList(vo);
        mav = new ModelAndView();
        mav.addObject("memberDelList", memberDelList);
        mav.setViewName("master/userDelMasterList");
        return mav;
    }

    // Dashboard - 애니관리 -  애니목록 리스트
    @GetMapping("/aniMasterList")
    public ModelAndView masterAniList() {
        System.out.println("관리자페이지 애니 리스트 불러오기");

        List<MasterVO> aniList = masterService.getAniAllList();

        mav = new ModelAndView();
        mav.addObject("aniList", aniList);
        mav.setViewName("master/aniMasterList");
        return mav;
    }


    // Dashboard - 회원관리 - 신고계정목록 리스트
    @GetMapping("/reportinguserListMaster")
    public ModelAndView masterReportList(MasterVO vo) {
        List<MasterVO> reportinguserList = masterService.getReportinguserList(vo);
        mav = new ModelAndView();
        mav.addObject("reportinguserList", reportinguserList);
        mav.setViewName("master/reportinguserListMaster");
        return mav;
    }


    // Dashboard - 애니관리 - 애니 목록 - 애니 추가
    @GetMapping("/aniAddMaster")
    public ModelAndView aniAddMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/aniAddMaster");
        return mav;
    }

    @PostMapping("/aniAddMasterOk")
    public String aniAddMasterOk(
            @RequestParam("title") String title,
            @RequestParam("director") String director,
            @RequestParam("outline") String outline,
            @RequestParam("post_img") MultipartFile post_img,
            @RequestParam("agetype") int agetype,
            @RequestParam("anitype") int anitype,
            @RequestParam("token") String token) {

        try {
            // JWT 토큰에서 adminid 추출
            String adminid = jwtUtil.getUserIdFromToken(token);

            // adminid로 adminidx 변환
            Integer adminidx = masterService.getAdminIdxByAdminid(adminid);
            if (adminidx == null) {
                // 처리 실패 (예: 관리자 정보를 찾을 수 없는 경우)
                return ""; // 실패 시 이동할 페이지 설정
            }

            // 파일 저장 로직
            String post_img_filename = null;
            if (post_img != null && !post_img.isEmpty()) {
                // 파일이 존재하면 저장
                post_img_filename = saveFile(post_img, "img/ani_img/");
            }

            // MasterVO 객체에 데이터 설정
            MasterVO aniVO = new MasterVO();
            aniVO.setTitle(title);
            aniVO.setDirector(director);
            aniVO.setOutline(outline);
            aniVO.setPost_img_filename(post_img_filename); // 저장된 파일명 설정
            aniVO.setAgetype(agetype);
            aniVO.setAnitype(anitype);
            aniVO.setAdminidx(adminidx); // adminidx 값 설정

            // 서비스 호출하여 애니메이션 추가
            masterService.addAnimation(aniVO);

            // 성공 시 페이지 리다이렉트
            return "redirect:/master/aniMasterList";

        } catch (Exception e) {
            e.printStackTrace();
            return ""; // 실패 시 에러 페이지 이동
        }
    }





    // Dashboard - 애니관리 - 애니 목록 - 애니 수정
    @GetMapping("/aniEditMaster/{idx}")
    public ModelAndView aniEditMaster(@PathVariable("idx") int idx){
        mav = new ModelAndView();
        mav.addObject("ani", masterService.aniSelect(idx));
        mav.setViewName("master/aniEditMaster");
        return mav;
    }

    @PostMapping("/aniEditMasterOk")
    public ModelAndView aniEditMasterOk(MasterVO vo,
                                        @RequestParam("idx") int idx,
                                        @RequestParam("post_img") MultipartFile post_img) throws IOException {
        mav = new ModelAndView();

        // 기존 post_img 값 가져오기 (DB에서 조회)
        String currentImg = masterService.getCurrentImgFile(idx);  // 기존 이미지 파일명 가져오기

        // 파일 처리 로직
        if (!post_img.isEmpty()) {
            // 파일이 저장될 로컬 경로 설정
            String uploadPath = new File("src/main/webapp/img/ani_img/").getAbsolutePath();
            String fileName = post_img.getOriginalFilename();
            File destination = new File(uploadPath + File.separator + fileName);

            // 디렉터리 생성 (존재하지 않으면)
            if (!destination.exists()) {
                destination.mkdirs();
            }

            post_img.transferTo(destination);  // 파일 저장
            vo.setPost_img_filename(fileName);  // 파일 이름 설정
        } else {
            vo.setPost_img_filename(currentImg);  // 업로드된 파일이 없으면 기존 파일명 유지
        }

        // 데이터베이스 업데이트
        masterService.updateAnimation(vo);

        mav.setViewName("redirect:/master/aniMasterList");
        return mav;
    }

    @PostMapping("/aniDeleteMaster/{idx}")
    public String aniDeleteMaster(@PathVariable("idx") int idx) {
        masterService.deletePostByIdx(idx);
        return "redirect:/master/aniMasterList";
    }

        // Dashboard - 굿즈관리 - 굿즈목록 리스트
        @GetMapping("/storeMasterList")
        public ModelAndView storeMasterList(){
            System.out.println("관리자페이지 굿즈 상품 테이블 불러오기");
            List<MasterVO> storeList = masterService.getStoreList();

            // 총 상품 수 구하기
            int totalStore = masterService.getTotalStore();

            Map<String, Object> categoryCode1Count = masterService.getCategoryCountByCode(1); // 의류
            Map<String, Object> categoryCode2Count = masterService.getCategoryCountByCode(2); // 완구/취미
            Map<String, Object> categoryCode3Count = masterService.getCategoryCountByCode(3); // 문구/오피스
            Map<String, Object> categoryCode4Count = masterService.getCategoryCountByCode(4); // 생활용품

            mav = new ModelAndView();
            mav.addObject("storeList", storeList);
            mav.addObject("totalStore", totalStore);
            mav.addObject("categoryCode1Count", categoryCode1Count.get("product_category"));
            mav.addObject("categoryCode2Count", categoryCode2Count.get("product_category"));
            mav.addObject("categoryCode3Count", categoryCode3Count.get("product_category"));
            mav.addObject("categoryCode4Count", categoryCode4Count.get("product_category"));
            mav.setViewName("master/storeMasterList");
            return mav;
        }

        // Dashboard - 주문관리 - 주문내역 리스트
        @GetMapping("/orderMasterList")
        public ModelAndView orderMasterList(){
            mav = new ModelAndView();
            mav.setViewName("master/orderMasterList");
            return mav;
        }

        // Dashboard - 신고관리 - 신고목록 리스트
        @GetMapping("/reportinguserMasterList")
        public ModelAndView reportinguserListMaster() {
            List<MasterVO> reportingUser = masterService.getReportingUser();  // 모든 신고된 유저 리스트를 가져옴

            // 각 유저별로 개별 신고 횟수를 계산
            for (MasterVO user : reportingUser) {
                int totalUserReport = masterService.getTotalUserReport(user.getUseridx());
                user.setTotalUserReport(totalUserReport);  // VO에 각 유저의 신고 횟수 저장
            }

            // 전체 신고 누적 횟수 계산
            int totalReportUser = masterService.getTotalReportCount();

            ModelAndView mav = new ModelAndView();
            mav.addObject("reportingUser", reportingUser);
            mav.addObject("totalReportUser", totalReportUser);
            mav.setViewName("master/reportinguserMasterList");
            return mav;
        }



    //  Dashboard - 게시판, 댓글, 리뷰 - 게시판 전체 목록
        @GetMapping("/boardMasterAll")
        public ModelAndView boardMasterAll(){
            // 커뮤니티 전체 글 목록 불러오기
            System.out.println("관리자페이지에서 커뮤니티 테이블 전체 글 목록 불러오기");
            List<MasterVO> boardList = masterService.getBoardList();
            mav = new ModelAndView();
            mav.addObject("boardList", boardList);
            mav.setViewName("master/boardMasterAll");
            return mav;
        }

    //  Dashboard - 게시판, 댓글, 리뷰 - 댓글 전체 목록
    @GetMapping("/boardMasterReviewAll")
    public ModelAndView boardMasterReviewAll(){
        List<MasterVO> reviewList = masterService.getReviewList();

        // 로그로 데이터 크기 확인
        System.out.println("불러온 댓글 개수: " + reviewList.size());

        ModelAndView mav = new ModelAndView();
        mav.addObject("reviewList", reviewList);
        mav.setViewName("master/boardMasterReviewAll");
        return mav;
    }

    //  Dashboard - 게시판, 댓글, 리뷰 - 리뷰 전체 목록
    @GetMapping("/boardMasterReplyAll")
    public ModelAndView boardMasterReplyAll(MasterVO vo){
            List<MasterVO> replyList = masterService.getReplyList(vo);
        mav = new ModelAndView();
        mav.addObject("replyList", replyList);
        mav.setViewName("master/boardMasterReplyAll");
        return mav;
    }

    @GetMapping("/getReviewDetail")
    public ResponseEntity<MasterVO> getReviewDetail(@RequestParam("idx") int idx) {
        // idx에 해당하는 리뷰 정보를 가져오기
        MasterVO review = masterService.getReviewDetail(idx);
        if (review != null) {
            try {
                // 이미지 파일이 있을 경우에만 URL 인코딩을 처리
                if (review.getImgfile1() != null && !review.getImgfile1().isEmpty()) {
                    String encodedImgFile1 = URLEncoder.encode(review.getImgfile1(), "UTF-8").replace("+", "%20");
                    review.setImgfile1(encodedImgFile1);
                } else {
                    review.setImgfile1(null); // 이미지가 없는 경우 처리
                }

                if (review.getImgfile2() != null && !review.getImgfile2().isEmpty()) {
                    String encodedImgFile2 = URLEncoder.encode(review.getImgfile2(), "UTF-8").replace("+", "%20");
                    review.setImgfile2(encodedImgFile2);
                } else {
                    review.setImgfile2(null); // 이미지가 없는 경우 처리
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(review, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    //  Dashboard - 기타관리 - 공지사항 목록
    @GetMapping("/noticeMasterList")
    public ModelAndView noticeMasterList(){
            // 관리자페이지 공지사항 글 목록 불러오기
        System.out.println("관리자페이지 공지사항 목록 불러오기");

        List<MasterVO> noticeList = masterService.getNoticeList();
        mav = new ModelAndView();
        mav.addObject("noticeList", noticeList);
        mav.setViewName("master/noticeMasterList");
        return mav;
    }
    //  Dashboard - 기타관리 - 공지사항 - 추가
    @GetMapping("/noticeAddMaster")
    public ModelAndView noticeAddMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/noticeAddMaster");
        return mav;
    }

    @PostMapping("/noticeAddMasterOk")
    public ResponseEntity<String> noticeAddMasterOk(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("token") String token) {

        String bodyTag = "";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8")));

        try {
            // JWT 토큰에서 adminid 추출
            String adminid = jwtUtil.getUserIdFromToken(token);

            // adminid를 통해 adminidx 조회
            Integer adminidx = masterService.getAdminIdxByAdminid(adminid);

            if (adminidx == null) {
                bodyTag += "<script>alert('관리자 정보를 찾을 수 없습니다.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.UNAUTHORIZED);
            }

            // 공지사항 등록 로직 (예: 데이터베이스에 공지사항 저장)
            MasterVO notice = new MasterVO();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setAdminidx(adminidx);  // adminidx 설정

            // 공지사항 데이터베이스에 삽입
            MasterVO resultNotice = masterService.createNotice(notice);

            if (resultNotice == null) {
                bodyTag += "<script>alert('공지사항 등록에 실패했습니다.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                bodyTag += "<script>alert('공지사항이 성공적으로 등록되었습니다.');location.href='/master/noticeMasterList';</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.OK);
            }

        } catch (Exception e) {
            log.error("공지사항 등록 중 오류 발생", e);
            bodyTag += "<script>alert('공지사항 등록 중 오류가 발생했습니다. 다시 시도해 주세요.');history.back();</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //  Dashboard - 기타관리 - 공지사항 - 수정
    @GetMapping("/noticeEditMaster")
    public ModelAndView noticeEditMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/noticeEditMaster");
        return mav;
    }

    //  Dashboard - 매출관리 - 일/월별 매출관리
    @GetMapping("/orderSalesMaster")
    public ModelAndView orderSalesMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/orderSalesMaster");
        return mav;
    }

    // Dashboard - 매출관리 - 일/월별 매출관리 - 상세보기
    @GetMapping("/orderSalesDetailMaster")
    public ModelAndView orderSalesDetailMaster(){
            mav = new ModelAndView();
            mav.setViewName("master/orderSalesDetailMaster");
        return mav;
    }

    // Dashboard - 매출관리 - 일/월별 매출관리 - 상세보기
    @GetMapping("/orderSalesDetail1Master")
    public ModelAndView orderSalesDetail1Master(){
        mav = new ModelAndView();
        mav.setViewName("master/orderSalesDetail1Master");
        return mav;
    }

    // Dashboard - 기타관리 - 문의사항 리스트
    @GetMapping("/QNAMasterList")
    public ModelAndView QNAMasterList(){
            List<MasterVO> qnaList = masterService.getQNAList();

            // 문의 사항 테이블에서 답변 안된 문의 개수 카운트
            int unanswerCount = masterService.getUnansweredQnaCount();
        mav = new ModelAndView();
        mav.addObject("qnaList", qnaList);
        mav.addObject("unanswerCount",unanswerCount);
        mav.setViewName("master/QNAMasterList");
        return mav;
    }

    @PostMapping("/QNAanswerOK")
    public String QNAanswerOK(@RequestParam("reply") String reply,
                              @RequestParam("idx") int idx,
                              HttpServletRequest request) {

        // 요청에서 Authorization 헤더를 확인
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("토큰이 없습니다.");
        }

        // JWT 토큰에서 사용자 정보 추출
        String token = authorizationHeader.substring(7);  // "Bearer " 부분을 제거
        Claims claims;
        try {
            claims = jwtUtil.getClaims(token);  // JWT 파싱하여 Claims 객체로 변환
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 관리자 ID 확인 (토큰에 담긴 정보)
        String adminId = claims.getSubject();  // 토큰에서 관리자 ID 추출
        Integer adminIdx = masterService.findAdminIdxByUserid(adminId);
        if (adminIdx == null) {
            throw new RuntimeException("유효하지 않은 관리자입니다.");
        }

        // QNA 답변 처리 로직
        masterService.updateQnaAndReply(idx, reply, adminIdx);

        return "redirect:/master/QNAMasterList";
    }



    // Dashboard - 기타관리 - 자주묻는질문
    @GetMapping("/FAQMasterList")
    public ModelAndView FAQMasterList(){
            // 자주묻는 질문 목록 불러오기
        System.out.println("자주묻는질문 목록 불러오기");
        List<MasterVO> faqList = masterService.getFAQList();
        mav = new ModelAndView();
        mav.addObject("faqList", faqList);
        mav.setViewName("master/FAQMasterList");
        return mav;
    }

    // Dashboard - 기타관리 - 자주묻는질문 - 작성
    @GetMapping("/FAQAddMaster")
    public ModelAndView FAQAddMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/FAQAddMaster");
        return mav;
    }

    @PostMapping("/FAQAddMasterOk")
    public ResponseEntity<String> FAQAddMasterOK(
            @RequestParam("code") String code,
            @RequestParam("question") String question,
            @RequestParam("answer") String answer,
            @RequestParam("token") String token){

            String bodyTag ="";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8")));

            try{
                // 토큰으로 adminid 추출
                String adminid = jwtUtil.getUserIdFromToken(token);

                // adminid를 adminidx로 변환
                Integer adminidx = masterService.getAdminIdxByAdminid(adminid);

                if(adminidx == null){
                    bodyTag += "<script>alert('관리자 정보를 찾을 수 없습니다.');history.back();</script>";
                    return new ResponseEntity<>(bodyTag, headers, HttpStatus.UNAUTHORIZED);
                }

                // 자주묻는 질문 등록 로직 (데이터베이스 저장)
                MasterVO faq = new MasterVO();
                faq.setFaqtype(code);
                faq.setQuestion(question);
                faq.setAnswer(answer);
                faq.setAdminidx(adminidx);

                MasterVO resultFaq = masterService.createFAQ(faq);

                if(resultFaq == null){
                    bodyTag += "<script>alert('FAQ 등록 실패. 다시 시도해 주세요.');history.back();</script>";
                    return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                }else{
                    bodyTag += "<script>alert('FAQ가 성공적으로 등록되었습니다.');location.href='/master/FAQMasterList';</script>";
                    return new ResponseEntity<>(bodyTag, headers, HttpStatus.OK);
                }
            }catch (Exception e){
                log.error("FAQ 등록 중 오류 발생", e);
                bodyTag += "<script>alert('FAQ 등록 중 오류가 발생했습니다. 다시 시도해 주세요.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

    // Dashboard - 기타관리 - 자주묻는질문 - 수정
    @GetMapping("/FAQEditMaster")
    public ModelAndView FAQEditMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/FAQEditMaster");
        return mav;
    }

    // Dashboard - 기타관리 - 자주묻는질문 - 삭제
    @GetMapping("/FAQDelMaster")
    public ModelAndView FAQDelMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/FAQDelMaster");
        return mav;
    }

    // Dashboard - 기타관리 - 이벤트
    @GetMapping("/EventMasterList")
    public ModelAndView EventMasterList(){
        mav = new ModelAndView();
        mav.setViewName("master/EventMasterList");
        return mav;
    }

    // Dashboard - 기타관리 - 이벤트 - 작성
    @GetMapping("/EventAddMaster")
    public ModelAndView EventAddMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/EventAddMaster");
        return mav;
    }

    // Dashboard - 기타관리 - 이벤트 - 수정
    @GetMapping("/EventEditMaster")
    public ModelAndView EventEditMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/EventEditMaster");
        return mav;
    }

    // Dashboard - 기타관리 - 이벤트 - 삭제
    @GetMapping("/EventDelMaster")
    public ModelAndView EventDelMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/EventDelMaster");
        return mav;
    }

    // Dashboard - 굿즈관리 - 상품 추가
    @GetMapping("/storeAddMaster")
    public ModelAndView storeAddMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/storeAddMaster");
        return mav;
    }

    // 굿즈 상품 데이터베이스 등록
    @PostMapping("/storeAddMasterOk")
    public ResponseEntity<String> storeAddMasterOK(
            @RequestParam("code") String code,
            @RequestParam("title") String title,
            @RequestParam("price") Integer price,
            @RequestParam("thumimg") MultipartFile thumimg,
            @RequestParam("ani_title") String ani_title,
            @RequestParam("relDT") String relDT,
            @RequestParam("brand") String brand,
            @RequestParam("pro_detail") MultipartFile pro_detail,
            @RequestParam("fee") int fee,
            @RequestParam("stock") int stock,
            @RequestParam("token") String token) {

        String bodyTag = "";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8")));

        try {
            // JWT 토큰에서 adminid 추출
            String adminid = jwtUtil.getUserIdFromToken(token);

            // adminid로 adminidx 변환
            Integer adminidx = masterService.getAdminIdxByAdminid(adminid);
            if (adminidx == null) {
                bodyTag += "<script>alert('관리자 정보를 찾을 수 없습니다.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.UNAUTHORIZED);
            }

            // 파일 저장 경로 설정
            String thumimgPath = saveFile(thumimg, "img/store/");
            String proDetailPath = saveFile(pro_detail, "img/store/");

            // 파일 저장 실패 시 처리
            if (thumimgPath == null || proDetailPath == null) {
                bodyTag += "<script>alert('파일 업로드에 실패했습니다.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 상품 등록 로직
            MasterVO storeAdd = new MasterVO();
            storeAdd.setCategory(code);
            storeAdd.setTitle(title);
            storeAdd.setPrice(price);
            storeAdd.setThumimg(thumimgPath);  // 저장된 썸네일 이미지 경로
            storeAdd.setAni_title(ani_title);
            storeAdd.setRelDT(relDT);
            storeAdd.setBrand(brand);
            storeAdd.setPro_detail(proDetailPath);  // 저장된 상세 정보 파일 경로
            storeAdd.setFee(fee);
            storeAdd.setStock(stock);
            storeAdd.setAdminidx(adminidx);

            // 서비스 호출하여 저장
            MasterVO resultStore = masterService.createStore(storeAdd);
            if (resultStore == null) {
                bodyTag += "<script>alert('굿즈 상품 등록 실패. 다시 시도해 주세요.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                bodyTag += "<script>alert('굿즈 상품이 성공적으로 등록되었습니다.');location.href='/master/storeMasterList';</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.OK);
            }
        } catch (IOException e) {
            log.error("파일 처리 중 오류 발생", e);
            bodyTag += "<script>alert('파일 처리 중 오류가 발생했습니다. 다시 시도해 주세요.');history.back();</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("굿즈 등록 중 오류 발생", e);
            bodyTag += "<script>alert('굿즈 상품 등록 중 오류가 발생했습니다. 다시 시도해 주세요.');history.back();</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 파일을 저장하는 메서드
    private String saveFile(MultipartFile file, String folderPath) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // 저장할 디렉터리 경로
        String absolutePath = new File("src/main/webapp/" + folderPath).getAbsolutePath();

        // 파일 이름 설정
        String originalFilename = file.getOriginalFilename();
        String filePath = absolutePath + "/" + originalFilename;
        File dest = new File(filePath);

        // 디렉터리 생성
        if (!dest.exists()) {
            dest.mkdirs();
        }

        // 파일 저장
        file.transferTo(dest);

        // 저장된 파일의 이름만 반환 (DB에 저장할 이름)
        return originalFilename;
    }

    // Dashboard - 굿즈관리 - 상품 수정
    @GetMapping("/storeEditMaster/{idx}")
    public String showStoreEditForm(@PathVariable("idx") int idx, Model model) {
        MasterVO Editstore = masterService.getStoreByIdx(idx); // idx를 이용해 store 데이터를 가져옴
        model.addAttribute("Editstore", Editstore); // JSP로 store 데이터를 전달
        return "master/storeEditMaster"; // storeEdit.jsp를 반환
    }

    @PostMapping("/storeEditMasterOK")
    public ResponseEntity<String> storeEditMasterOK(
            @RequestParam("idx") int idx,
            @RequestParam("category") String category,
            @RequestParam("second_category") int secondCategory,
            @RequestParam("title") String title,
            @RequestParam("price") Integer price,
            @RequestParam(value = "thumimg", required = false) MultipartFile thumimg,
            @RequestParam("ani_title") String ani_title,
            @RequestParam("relDT") String relDT,
            @RequestParam("brand") String brand,
            @RequestParam(value = "detailImg", required = false) MultipartFile detailImg,
            @RequestParam("fee") int fee,
            @RequestParam("stock") int stock,
            @RequestParam(value = "detaillmg", required = false) MultipartFile detaillmg, // 추가 이미지 파일
            @RequestParam("token") String token) {

        String bodyTag = "";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8")));

        try {
            // JWT 토큰 검증
            if (token == null || token.trim().isEmpty()) {
                bodyTag += "<script>alert('유효하지 않은 토큰입니다. 다시 로그인 해주세요.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.UNAUTHORIZED);
            }

            String adminid = jwtUtil.getUserIdFromToken(token);
            Integer adminidx = masterService.getAdminIdxByAdminid(adminid);
            if (adminidx == null) {
                bodyTag += "<script>alert('관리자 정보를 찾을 수 없습니다.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.UNAUTHORIZED);
            }

            // 기존 상품 정보 불러오기
            MasterVO store = masterService.getStoreByIdx(idx);
            if (store == null) {
                bodyTag += "<script>alert('해당 상품을 찾을 수 없습니다.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.NOT_FOUND);
            }

            // 썸네일 이미지 및 상세 이미지 경로 처리
            String thumimgPath = null;
            String proDetailPath = null;
            String detailImgPath = null;

            if (thumimg != null && !thumimg.isEmpty()) {
                thumimgPath = saveFile(thumimg, "img/store/");
            }

            if (detailImg != null && !detailImg.isEmpty()) {
                proDetailPath = saveFile(detailImg, "img/store/");
            }

            if (detaillmg != null && !detaillmg.isEmpty()) {
                detailImgPath = saveFile(detaillmg, "img/store/");
            }

            // 상품 정보 수정
            store.setCategory(category);
            store.setSecond_category(secondCategory);
            store.setTitle(title);
            store.setPrice(price);
            store.setAni_title(ani_title);
            store.setRelDT(relDT);
            store.setBrand(brand);
            store.setFee(fee);
            store.setStock(stock);
            store.setAdminidx(adminidx);

            if (thumimgPath != null) {
                store.setThumimg(thumimgPath);
            }
            if (proDetailPath != null) {
                store.setPro_detail(proDetailPath);
            }

            boolean updateResult = masterService.updateStore(store);
            if (!updateResult) {
                bodyTag += "<script>alert('굿즈 상품 수정 실패. 다시 시도해 주세요.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 상세 이미지가 존재하면 t_productimg 테이블에 저장
            if (detailImgPath != null) {
                MasterVO productImg = new MasterVO();
                productImg.setPro_idx(idx); // 상품의 idx를 참조
                productImg.setDetailImg(detailImgPath); // 저장된 이미지 경로

                boolean imgInsertResult = masterService.insertProductImg(productImg);
                if (!imgInsertResult) {
                    bodyTag += "<script>alert('이미지 저장 실패. 다시 시도해 주세요.');history.back();</script>";
                    return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            // 성공 시 리다이렉트
            bodyTag += "<script>alert('굿즈 상품이 성공적으로 수정되었습니다.');location.href='/master/storeMasterList';</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("굿즈 상품 수정 중 오류 발생", e);
            bodyTag += "<script>alert('굿즈 상품 수정 중 오류가 발생했습니다. 다시 시도해 주세요.');history.back();</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/getSubCategories/{category}")
    @ResponseBody
    public List<MasterVO> getSubCategories(@PathVariable("category") int category) {
        return masterService.getSubCategoriesByCategory(category);
    }




    // Dashboard - 굿즈관리 - 상품 수정
    @GetMapping("/orderEditMaster")
    public ModelAndView orderEditMaster(){
        mav = new ModelAndView();
        mav.setViewName("master/orderEditMaster");
        return mav;
    }

    //관리자 로그인 페이지 view
    @GetMapping("/masterLogin")
    public ModelAndView masterLogin(){
        mav = new ModelAndView();
        mav.setViewName("join/admin_login");

        return mav;
    }

    @PostMapping("/reportinguserOK")
    public String reportinguserOK(@RequestParam("userid") String userid,
                                  @RequestParam("reason") String reason,
                                  @RequestParam("handleDT") String handleDT,  // 처리 날짜
                                  @RequestParam("endDT") String endDT,        // 제재 종료 날짜
                                  @RequestParam("handleState") int handleState, // 처리 상태 코드
                                  @RequestParam("idx") int idx,               // 신고 ID
                                  HttpServletRequest request) {
        System.out.println("Received idx: " + idx);  // idx 값 확인을 위해 콘솔에 출력
        System.out.println("Received userid: " + userid);

        LocalDateTime stopDT = LocalDateTime.now();  // 신고 시작 시간

        // handleDT 및 endDT를 LocalDateTime으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime parsedHandleDT = LocalDate.parse(handleDT, formatter).atStartOfDay();
        LocalDateTime parsedEndDT = LocalDate.parse(endDT, formatter).atStartOfDay();

        // 헤더에서 Authorization 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("토큰이 없습니다.");
        }

        Integer useridx = masterService.findUserIdxByUserid(userid);
        if (useridx == null) {
            throw new RuntimeException("유효하지 않은 사용자입니다.");
        }

        String token = authorizationHeader.substring(7);  // "Bearer " 부분을 제거한 JWT 토큰
        Claims claims;
        try {
            claims = jwtUtil.getClaims(token);  // JWT 파싱하여 Claims 객체로 변환
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 사용자 정지 여부 확인
        boolean isBanned = masterService.checkUserBanStatus(userid);
        if (isBanned) {
            return "redirect:/master/reportinguserListMaster";  // 이미 정지된 사용자는 처리할 필요 없음
        }

        // 서비스에 신고 내역 추가 요청
        masterService.updateReportAndBan(idx, userid, reason, stopDT, parsedHandleDT, parsedEndDT, handleState);

        return "redirect:/master/reportinguserListMaster";  // 신고 목록 페이지로 리다이렉트
    }
}
