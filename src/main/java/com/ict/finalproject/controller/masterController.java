package com.ict.finalproject.controller;


import com.ict.finalproject.DAO.TAdminDAO;
import com.ict.finalproject.JWT.JWTUtil;
import com.ict.finalproject.Service.MasterService;
import com.ict.finalproject.Service.MemberService;
import com.ict.finalproject.Service.TAdminService;
import com.ict.finalproject.vo.MasterVO;
import com.ict.finalproject.vo.MemberVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    @GetMapping("/checkAdmin")
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

        try {
            // 토큰에서 사용자 ID 추출
            String userid = jwtUtil.getUserIdFromToken(token);
            System.out.println("JWT 토큰에서 추출한 사용자 ID: " + userid);

            // t_admin 테이블에서 해당 adminid가 존재하는지 확인
            boolean isAdmin = tAdminService.existsByAdminId(userid);
            System.out.println("t_admin 테이블에 " + userid + " 존재 여부: " + isAdmin);

            // 관리자 여부를 반환
            return ResponseEntity.ok(isAdmin);

        } catch (Exception e) {
            // 오류 발생 시 로그 출력 및 UNAUTHORIZED 응답
            System.out.println("JWT 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }


    @ModelAttribute("unansweredCount")
    public int unansweredQnaCount() {
        return masterService.getUnansweredQnaCount();  // 미답변 문의 수 조회
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
    public ModelAndView masterAniList(@RequestParam(value = "currentPage", defaultValue = "1") double currentPage,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        int currentPageInt = (int) Math.floor(currentPage); // 정수로 변환
        int offset = Math.max(0, (currentPageInt - 1) * pageSize);

        // 전체 애니메이션 수 구하기
        int totalAniCount = masterService.getTotalAnimeCount();
        int totalPages = (int) Math.ceil((double) totalAniCount / pageSize);

        List<MasterVO> aniList = masterService.getAniListWithPaging(offset, pageSize);

        ModelAndView mav = new ModelAndView();
        mav.addObject("aniList", aniList);
        mav.addObject("currentPage", currentPageInt);
        mav.addObject("pageSize", pageSize);
        mav.addObject("totalPages", totalPages);
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
    public ResponseEntity<String> aniAddMasterOk(
            @RequestParam("title") String title,
            @RequestParam("director") String director,
            @RequestParam("outline") String outline,
            @RequestParam(value = "post_img", required = false) MultipartFile post_img,
            @RequestParam("agetype") int agetype,
            @RequestParam("anitype") int anitype,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Authorization 헤더 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }

        // JWT 토큰에서 관리자 ID 추출
        String token = authorizationHeader.substring(7);  // "Bearer " 부분을 제거
        String adminid;
        try {
            adminid = jwtUtil.getUserIdFromToken(token); // JWT에서 관리자 ID 추출
            if (adminid == null || adminid.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 JWT 토큰입니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 토큰 파싱 중 오류가 발생했습니다.");
        }

        // adminid로 adminidx 변환
        Integer adminidx = masterService.getAdminIdxByAdminid(adminid);
        if (adminidx == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 정보를 찾을 수 없습니다.");
        }

        String post_img_filename = null;
        try {
            // 파일이 존재하면 외부 서버로 업로드
            if (post_img != null && !post_img.isEmpty()) {
                post_img_filename = uploadFileToExternalServer(post_img);
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

            // 성공 시 응답
            return ResponseEntity.ok("애니메이션이 성공적으로 추가되었습니다.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("애니메이션 추가 중 오류가 발생했습니다.");
        }
    }





    private String uploadFileToExternalServer(MultipartFile file) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String imageServerUrl = "http://192.168.1.92:8000/upload"; // 이미지 서버의 파일 업로드 엔드포인트

        // 파일을 MultiValueMap으로 준비
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), uniqueFilename));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 이미지 서버로 파일 전송
        ResponseEntity<String> response = restTemplate.postForEntity(imageServerUrl, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // 서버에서 성공적으로 응답한 경우, 저장할 파일명만 반환
            return uniqueFilename; // URL이 아닌 파일명만 반환
        } else {
            throw new IOException("파일 업로드 실패: " + response.getStatusCode());
        }
    }


    class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // length를 모르는 경우 -1 반환
        }
    }



    // Dashboard - 애니관리 - 애니 목록 - 애니 수정
    @GetMapping("/aniEditMaster/{idx}")
    public ModelAndView aniEditMaster(@PathVariable("idx") int idx) {
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
    public ModelAndView storeMasterList(@RequestParam(value = "currentPage", defaultValue = "1") String currentPageStr,
                                        @RequestParam(value = "pageSize", defaultValue = "10") String pageSizeStr) {
        System.out.println("관리자페이지 굿즈 상품 테이블 불러오기");

        // currentPage와 pageSize를 정수형으로 변환
        int currentPage;
        int pageSize;
        try {
            // 소수점이 포함된 경우 정수로 변환
            currentPage = (int) Double.parseDouble(currentPageStr);
            pageSize = (int) Double.parseDouble(pageSizeStr);
        } catch (NumberFormatException e) {
            // 변환에 실패하면 기본값으로 설정
            currentPage = 1;
            pageSize = 10;
        }

        // 페이징 로직 추가
        int offset = Math.max(0, (currentPage - 1) * pageSize);
        List<MasterVO> storeList = masterService.getStoreListWithPaging(offset, pageSize);

        // 총 상품 수 구하기
        int totalStore = masterService.getTotalStore();
        int totalPages = (int) Math.ceil((double) totalStore / pageSize);

        // 카테고리별 상품 수 구하기
        Map<String, Object> categoryCode1Count = masterService.getCategoryCountByCode(1); // 의류
        Map<String, Object> categoryCode2Count = masterService.getCategoryCountByCode(2); // 완구/취미
        Map<String, Object> categoryCode3Count = masterService.getCategoryCountByCode(3); // 문구/오피스
        Map<String, Object> categoryCode4Count = masterService.getCategoryCountByCode(4); // 생활용품

        ModelAndView mav = new ModelAndView();
        mav.addObject("storeList", storeList);
        mav.addObject("totalStore", totalStore);
        mav.addObject("categoryCode1Count", categoryCode1Count.get("product_category"));
        mav.addObject("categoryCode2Count", categoryCode2Count.get("product_category"));
        mav.addObject("categoryCode3Count", categoryCode3Count.get("product_category"));
        mav.addObject("categoryCode4Count", categoryCode4Count.get("product_category"));
        mav.addObject("currentPage", currentPage);
        mav.addObject("pageSize", pageSize);
        mav.addObject("totalPages", totalPages);
        mav.setViewName("master/storeMasterList");
        return mav;
    }



    // Dashboard - 주문관리 - 주문내역 리스트
    @GetMapping("/orderMasterList")
    public ModelAndView orderMasterList() {
        mav = new ModelAndView();
        mav.setViewName("master/orderMasterList");
        return mav;
    }

    // Dashboard - 신고관리 - 신고목록 리스트
    @GetMapping("/reportinguserMasterList")
    public ModelAndView reportinguserListMaster(
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        int offset = (currentPage - 1) * pageSize;

        // 페이징을 적용한 신고된 유저 목록 조회
        List<MasterVO> reportingUser = masterService.getReportingUserWithPaging(offset, pageSize);

        // 각 유저별로 개별 신고 횟수를 계산
        for (MasterVO user : reportingUser) {
            int totalUserReport = masterService.getTotalUserReport(user.getUseridx());
            user.setTotalUserReport(totalUserReport);
        }

        // 전체 신고 누적 횟수 계산
        int totalReportUser = masterService.getTotalReportCount();
        int totalUsers = masterService.getTotalReportingUserCount();
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        ModelAndView mav = new ModelAndView();
        mav.addObject("reportingUser", reportingUser);
        mav.addObject("totalReportUser", totalReportUser);
        mav.addObject("currentPage", currentPage);
        mav.addObject("totalPages", totalPages);
        mav.addObject("pageSize", pageSize);
        mav.setViewName("master/reportinguserMasterList");
        return mav;
    }



    //  Dashboard - 게시판, 댓글, 리뷰 - 게시판 전체 목록
    @GetMapping("/boardMasterAll")
    public ModelAndView boardMasterAll(
            @RequestParam(value = "currentPage", defaultValue = "1") String currentPage,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        System.out.println("관리자페이지에서 커뮤니티 테이블 전체 글 목록 불러오기");

        // currentPage를 정수형으로 변환
        int currentPageInt;
        try {
            currentPageInt = Integer.parseInt(currentPage);
        } catch (NumberFormatException e) {
            // 변환 실패 시 기본값 1로 설정
            currentPageInt = 1;
        }

        // 페이징 로직 추가
        int offset = Math.max(0, (currentPageInt - 1) * pageSize);
        List<MasterVO> boardList = masterService.getBoardListWithPaging(offset, pageSize);

        // 총 게시글 수 구하기
        int totalBoard = masterService.getTotalBoardCount();
        int totalPages = (int) Math.ceil((double) totalBoard / pageSize);

        ModelAndView mav = new ModelAndView();
        mav.addObject("boardList", boardList);
        mav.addObject("currentPage", currentPageInt);
        mav.addObject("pageSize", pageSize);
        mav.addObject("totalPages", totalPages);
        mav.setViewName("master/boardMasterAll");
        return mav;
    }



    //  Dashboard - 게시판, 댓글, 리뷰 - 댓글 전체 목록
    @GetMapping("/boardMasterReviewAll")
    public ModelAndView boardMasterReviewAll(
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        // 페이징 계산
        int offset = Math.max(0, (currentPage - 1) * pageSize);
        List<MasterVO> reviewList = masterService.getReviewListWithPaging(offset, pageSize);

        // 전체 댓글 수
        int totalReviews = masterService.getTotalReviewCount();
        int totalPages = (int) Math.ceil((double) totalReviews / pageSize);

        // 로그로 데이터 크기 확인
        System.out.println("불러온 댓글 개수: " + reviewList.size());

        ModelAndView mav = new ModelAndView();
        mav.addObject("reviewList", reviewList);
        mav.addObject("currentPage", currentPage);
        mav.addObject("pageSize", pageSize);
        mav.addObject("totalPages", totalPages);
        mav.setViewName("master/boardMasterReviewAll");
        return mav;
    }


    //  Dashboard - 게시판, 댓글, 리뷰 - 리뷰 전체 목록
    @GetMapping("/boardMasterCommentAll")
    public ModelAndView boardMasterCommentAll(
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        int offset = (currentPage - 1) * pageSize;

        // 페이징을 적용한 댓글 목록 조회
        List<MasterVO> replyList = masterService.getReplyListWithPaging(offset, pageSize);

        // 전체 댓글 개수 조회
        int totalReplies = masterService.getTotalReplyCount();
        int totalPages = (int) Math.ceil((double) totalReplies / pageSize);

        ModelAndView mav = new ModelAndView();
        mav.addObject("replyList", replyList);
        mav.addObject("currentPage", currentPage);
        mav.addObject("totalPages", totalPages);
        mav.addObject("pageSize", pageSize);
        mav.setViewName("master/boardMasterCommentAll");
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
    public ModelAndView noticeMasterList() {
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
    public ModelAndView noticeAddMaster() {
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
    @GetMapping("/noticeEditMaster/{idx}")
    public ModelAndView noticeEditMaster(@PathVariable("idx") int idx) {
        mav = new ModelAndView();
        mav.addObject("noticeEdit", masterService.noticeSelect(idx));
        mav.setViewName("master/noticeEditMaster");
        return mav;
    }

    @PostMapping("/noticeEditMasterOk")
    public ResponseEntity<String> noticeEditMasterOk(
            @RequestParam(value = "idx", required = false, defaultValue = "0") int idx,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("token") String token) {

        log.info("전달된 토큰: " + token);

        String bodyTag = "";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        // idx 값 검증
        if (idx == 0) {
            bodyTag += "<script>alert('공지사항 ID가 잘못되었습니다.');history.back();</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.BAD_REQUEST);
        }

        try {
            // JWT 토큰 검증
            if (token == null || token.trim().isEmpty()) {
                bodyTag += "<script>alert('유효하지 않은 토큰입니다. 다시 로그인 해주세요.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.UNAUTHORIZED);
            }

            String adminid = jwtUtil.getUserIdFromToken(token);
            log.info("추출된 사용자 ID: " + adminid);

            if (adminid == null) {
                log.error("토큰에서 사용자 ID를 추출할 수 없습니다.");
                return new ResponseEntity<>("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
            }

            // adminId로 adminidx 추출
            Integer adminidx = masterService.getAdminIdxByAdminid(adminid);
            log.info("추출된 adminidx: " + adminidx); // adminidx 로그 추가

            if (adminidx == null) {
                log.error("adminidx가 null입니다.");
                bodyTag += "<script>alert('관리자 정보를 찾을 수 없습니다.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.UNAUTHORIZED);
            }

            // 공지사항 정보 불러오기
            MasterVO Editnotice = masterService.getNoticeById(idx);
            if (Editnotice == null) {
                bodyTag += "<script>alert('해당 공지사항을 찾을 수 없습니다.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.NOT_FOUND);
            }

            // 공지사항 정보 업데이트
            Editnotice.setTitle(title);
            Editnotice.setContent(content);
            Editnotice.setAdminidx(adminidx);

            boolean updateResult = masterService.updateNotice(Editnotice);
            if (!updateResult) {
                bodyTag += "<script>alert('공지사항 수정 실패. 다시 시도해 주세요.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 성공 시 리다이렉트
            bodyTag += "<script>alert('공지사항이 성공적으로 수정되었습니다.');location.href='/master/noticeList';</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.OK);

        } catch (ExpiredJwtException e) {
            log.error("토큰이 만료되었습니다.", e);
            bodyTag += "<script>alert('토큰이 만료되었습니다. 다시 로그인 해주세요.');history.back();</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생", e);
            bodyTag += "<script>alert('공지사항 수정 중 오류가 발생했습니다. 다시 시도해 주세요.');history.back();</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //  Dashboard - 매출관리 - 일/월별 매출관리
    @GetMapping("/orderSalesMaster")
    public ModelAndView orderSalesMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/orderSalesMaster");
        return mav;
    }

    // Dashboard - 매출관리 - 일/월별 매출관리 - 상세보기
    @GetMapping("/orderSalesDetailMaster")
    public ModelAndView orderSalesDetailMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/orderSalesDetailMaster");
        return mav;
    }

    // Dashboard - 매출관리 - 일/월별 매출관리 - 상세보기
    @GetMapping("/orderSalesDetail1Master")
    public ModelAndView orderSalesDetail1Master() {
        mav = new ModelAndView();
        mav.setViewName("master/orderSalesDetail1Master");
        return mav;
    }

    // Dashboard - 기타관리 - 문의사항 리스트
    @GetMapping("/QNAMasterList")
    public ModelAndView QNAMasterList() {
        List<MasterVO> qnaList = masterService.getQNAList();

        // 문의 사항 테이블에서 답변 안된 문의 개수 카운트
        int unanswerCount = masterService.getUnansweredQnaCount();
        mav = new ModelAndView();
        mav.addObject("qnaList", qnaList);
        mav.addObject("unanswerCount", unanswerCount);
        mav.setViewName("master/QNAMasterList");
        return mav;
    }

    // 답변 내용 확인하기
    @GetMapping("/getQnaReply/{idx}")
    public ResponseEntity<MasterVO> getQnaReply(@PathVariable("idx") int idx) {
        try {
            // idx에 해당하는 QnA 데이터를 서비스에서 가져옴
            MasterVO qna = masterService.getQnaById(idx);

            if (qna != null) {
                return new ResponseEntity<>(qna, HttpStatus.OK); // JSON 형태로 반환
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public ModelAndView FAQMasterList() {
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
    public ModelAndView FAQAddMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/FAQAddMaster");
        return mav;
    }

    @PostMapping("/FAQAddMasterOk")
    public ResponseEntity<String> FAQAddMasterOK(
            @RequestParam("code") String code,
            @RequestParam("question") String question,
            @RequestParam("answer") String answer,
            @RequestParam("token") String token) {

        String bodyTag = "";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8")));

        try {
            // 토큰으로 adminid 추출
            String adminid = jwtUtil.getUserIdFromToken(token);

            // adminid를 adminidx로 변환
            Integer adminidx = masterService.getAdminIdxByAdminid(adminid);

            if (adminidx == null) {
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

            if (resultFaq == null) {
                bodyTag += "<script>alert('FAQ 등록 실패. 다시 시도해 주세요.');history.back();</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                bodyTag += "<script>alert('FAQ가 성공적으로 등록되었습니다.');location.href='/master/FAQMasterList';</script>";
                return new ResponseEntity<>(bodyTag, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("FAQ 등록 중 오류 발생", e);
            bodyTag += "<script>alert('FAQ 등록 중 오류가 발생했습니다. 다시 시도해 주세요.');history.back();</script>";
            return new ResponseEntity<>(bodyTag, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Dashboard - 기타관리 - 자주묻는질문 - 수정
    @GetMapping("/FAQEditMaster")
    public ModelAndView FAQEditMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/FAQEditMaster");
        return mav;
    }

    // Dashboard - 기타관리 - 자주묻는질문 - 삭제
    @GetMapping("/FAQDelMaster")
    public ModelAndView FAQDelMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/FAQDelMaster");
        return mav;
    }

    // Dashboard - 기타관리 - 이벤트
    @GetMapping("/EventMasterList")
    public ModelAndView EventMasterList() {
        List<MasterVO> eventList = masterService.getEventList();
        mav = new ModelAndView();
        mav.addObject("eventList", eventList);
        mav.setViewName("master/EventMasterList");
        return mav;
    }

    // Dashboard - 기타관리 - 이벤트 - 작성
    @GetMapping("/EventAddMaster")
    public ModelAndView EventAddMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/EventAddMaster");
        return mav;
    }

    // Dashboard - 기타관리 - 이벤트 - 수정
    @GetMapping("/EventEditMaster/{idx}")
    public ModelAndView EventEditMaster(@PathVariable("idx") int idx) {
        ModelAndView mav = new ModelAndView();

        // 해당 idx의 이벤트 정보를 조회
        MasterVO event = masterService.getEventByIdx(idx);
        if (event == null) {
            // 이벤트가 존재하지 않을 경우 에러 페이지로 이동
            mav.setViewName("error");
            mav.addObject("message", "해당 이벤트를 찾을 수 없습니다.");
            return mav;
        }

        // 이벤트 정보가 존재하면 뷰에 전달
        mav.setViewName("master/EventEditMaster");
        mav.addObject("event", event);

        return mav;
    }

    // Dashboard - 기타관리 - 이벤트 - 삭제
    @GetMapping("/EventDelMaster")
    public ModelAndView EventDelMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/EventDelMaster");
        return mav;
    }

    // Dashboard - 굿즈관리 - 상품 추가
    @GetMapping("/storeAddMaster")
    public ModelAndView storeAddMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/storeAddMaster");
        return mav;
    }

    private String getAdminIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("토큰이 없습니다.");
        }

        String token = authorizationHeader.substring(7);  // "Bearer " 부분을 제거
        String adminid = jwtUtil.getUserIdFromToken(token);  // JWT에서 관리자 ID 추출

        if (adminid == null || adminid.isEmpty()) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        }

        return adminid;
    }

    @PostMapping("/storeAddMasterOk")
    public String storeAddMasterOK(
            @RequestParam("code") String code,
            @RequestParam("title") String title,
            @RequestParam("price") int price,
            @RequestParam(value = "thumImg", required = false) MultipartFile thumImg,
            @RequestParam("ani_title") String ani_title,
            @RequestParam("relDT") String relDT,
            @RequestParam("brand") String brand,
            @RequestParam("pro_detail") String pro_detail,
            @RequestParam("fee") int fee,
            @RequestParam("stock") int stock,
            @RequestParam("second_category") int second_category,
            @RequestParam(value = "detailImg", required = false) MultipartFile detailImg,
            @RequestHeader("Authorization") String authorizationHeader) {

        System.out.println("Received Authorization Header: " + authorizationHeader);

        // Authorization 헤더 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return "redirect:/user/login"; // 토큰이 없을 경우 로그인 페이지로 리디렉션
        }

        // JWT 토큰에서 관리자 ID 추출
        String token = authorizationHeader.substring(7);  // "Bearer " 부분을 제거
        String adminid;
        int pro_idx;
        try {
            adminid = jwtUtil.getUserIdFromToken(token); // JWT에서 관리자 ID 추출
            if (adminid == null || adminid.isEmpty()) {
                return "redirect:/user/login"; // 유효하지 않은 토큰일 경우 로그인 페이지로 리디렉션
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/user/login"; // JWT 파싱 중 오류 발생 시 로그인 페이지로 리디렉션
        }

        // adminid로 adminidx 변환
        Integer adminidx = masterService.getAdminIdxByAdminid(adminid);
        if (adminidx == null) {
            return "redirect:/user/login"; // 관리자 정보를 찾을 수 없을 경우 로그인 페이지로 리디렉션
        }

        // 파일 저장 로직
        String thumimg_filename = null;
        String detailImg_filename = null;

        try {
            if (thumImg != null && !thumImg.isEmpty()) {
                thumimg_filename = uploadFileToExternalServer(thumImg);
            }
            if (detailImg != null && !detailImg.isEmpty()) {
                detailImg_filename = uploadFileToExternalServer(detailImg);
            }

            // t_product 테이블에 데이터 삽입
            MasterVO storeAdd = new MasterVO();
            storeAdd.setCategory(code);
            storeAdd.setTitle(title);
            storeAdd.setPrice(price);
            storeAdd.setThumImg(thumimg_filename);
            storeAdd.setAni_title(ani_title);
            storeAdd.setRelDT(relDT);
            storeAdd.setBrand(brand);
            storeAdd.setPro_detail(pro_detail);
            storeAdd.setFee(fee);
            storeAdd.setStock(stock);
            storeAdd.setSecond_category(second_category);
            storeAdd.setAdminidx(adminidx);

            // t_product 테이블에 데이터 삽입 후 idx 반환
            pro_idx = masterService.createStore(storeAdd);
            System.out.println("생성된 pro_idx: " + pro_idx); // 생성된 idx 값을 확인

            // pro_idx가 유효한지 확인
            if (pro_idx <= 0) {
                throw new RuntimeException("유효하지 않은 pro_idx입니다.");
            }

            // t_productimg 테이블에 데이터 삽입
            MasterVO productImg = new MasterVO();
            productImg.setPro_idx(pro_idx); // 생성된 pro_idx를 설정
            productImg.setDetailImg(detailImg_filename);
            masterService.insertProductImg(productImg);

            // 성공적으로 등록되었을 때 리스트 페이지로 리디렉션
            return "redirect:/master/storeMasterList";

        } catch (Exception e) {
            e.printStackTrace();
            // 오류 발생 시 에러 페이지로 리디렉션
            return "redirect:/errorPage"; // 적절한 에러 페이지 설정
        }
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
            @RequestParam("code") String code,
            @RequestParam(value = "second_category", required = false, defaultValue = "0") int second_category,
            @RequestParam("title") String title,
            @RequestParam("price") Integer price,
            @RequestParam(value = "thumImg", required = false) MultipartFile thumImg,
            @RequestParam("ani_title") String ani_title,
            @RequestParam("relDT") String relDT,
            @RequestParam("brand") String brand,
            @RequestParam("pro_detail") String pro_detail,
            @RequestParam("fee") int fee,
            @RequestParam("stock") int stock,
            @RequestParam(value = "detailImg", required = false) MultipartFile detailImg,
            @RequestHeader("Authorization") String authorizationHeader) {

        // Authorization 헤더 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }

        // JWT 토큰에서 관리자 ID 추출
        String token = authorizationHeader.substring(7);
        String adminid;
        try {
            adminid = jwtUtil.getUserIdFromToken(token);
            if (adminid == null || adminid.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 JWT 토큰입니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 토큰 파싱 중 오류가 발생했습니다.");
        }

        // adminid로 adminidx 변환
        Integer adminidx = masterService.getAdminIdxByAdminid(adminid);
        if (adminidx == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 정보를 찾을 수 없습니다.");
        }

        // 기존 상품 정보 불러오기
        MasterVO store = masterService.getStoreByIdx(idx);
        if (store == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 상품을 찾을 수 없습니다.");
        }

        // 파일 저장 로직
        String thumimg_filename = null;
        String detailImg_filename = null;

        try {
            if (thumImg != null && !thumImg.isEmpty()) {
                thumimg_filename = uploadFileToExternalServer(thumImg);
            }
            if (detailImg != null && !detailImg.isEmpty()) {
                detailImg_filename = uploadFileToExternalServer(detailImg);
            }

            // 상품 정보 수정
            store.setCategory(code);
            store.setSecond_category(second_category);
            store.setTitle(title);
            store.setPrice(price);
            store.setAni_title(ani_title);
            store.setRelDT(relDT);
            store.setBrand(brand);
            store.setPro_detail(pro_detail);
            store.setFee(fee);
            store.setStock(stock);
            store.setAdminidx(adminidx);

            if (thumimg_filename != null) {
                store.setThumImg(thumimg_filename);
            }

            boolean updateResult = masterService.updateStore(store);
            if (!updateResult) {
                throw new RuntimeException("굿즈 상품 수정 실패");
            }

            // detailImg 업데이트 로직
            if (detailImg_filename != null) {
                MasterVO productImg = masterService.getProductImgByIdx(idx);
                if (productImg == null) {
                    // 기존 이미지 데이터가 없으면 새로 삽입
                    productImg = new MasterVO();
                    productImg.setPro_idx(idx);
                    productImg.setDetailImg(detailImg_filename);
                    masterService.insertProductImg(productImg);
                } else {
                    // 기존 이미지 데이터가 있으면 업데이트
                    productImg.setDetailImg(detailImg_filename);
                    masterService.updateProductImg(productImg);
                }
            }

            return ResponseEntity.ok().body("굿즈 상품이 성공적으로 수정되었습니다.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("굿즈 상품 수정 중 오류가 발생했습니다.");
        }
    }


    @PostMapping("/storeDeleteMaster/{idx}")
    public String storeDeleteMaster(@PathVariable("idx") int idx) {
        masterService.deleteProductImagesByProductIdx(idx);
        masterService.deleteStoreByIdx(idx);
        return "redirect:/master/storeMasterList";
    }


    @GetMapping("/getSubCategories/{category}")
    @ResponseBody
    public List<MasterVO> getSubCategories(@PathVariable("category") int category) {
        return masterService.getSubCategoriesByCategory(category);
    }


    // Dashboard - 굿즈관리 - 상품 수정
    @GetMapping("/orderEditMaster")
    public ModelAndView orderEditMaster() {
        mav = new ModelAndView();
        mav.setViewName("master/orderEditMaster");
        return mav;
    }

    //관리자 로그인 페이지 view
    @GetMapping("/masterLogin")
    public ModelAndView masterLogin() {
        mav = new ModelAndView();
        mav.setViewName("join/admin_login");
        return mav;
    }

    // 관리자 로그인
    @PostMapping("/masterLoginOK")
    public ResponseEntity<Map<String, String>> masterLoginOK(@RequestBody Map<String, String> request) {
        String adminid = request.get("adminid");
        String adminpwd = request.get("adminpwd");

        try {
            // 관리자 아이디와 비밀번호 확인
            if (masterService.validateAdmin(adminid, adminpwd)) {
                String token = jwtUtil.generateAdminToken(adminid);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);  // JSON 형식으로 반환
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("errorMessage", "로그인 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("errorMessage", "서버 에러");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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

    // 이벤트 페이지 글 쓰기
    @PostMapping("/EventAddMasterOk")
    public ResponseEntity<String> EventAddMasterOk(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("event_date") String event_date,
            @RequestParam("thumfile") MultipartFile thumfile,
            @RequestHeader("Authorization") String authorizationHeader) {

        System.out.println("Received title: " + title);

        // Authorization 헤더 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }

        // JWT 토큰에서 관리자 ID 추출
        String token = authorizationHeader.substring(7);  // "Bearer " 부분을 제거
        String adminid;
        try {
            adminid = jwtUtil.getUserIdFromToken(token); // JWT에서 관리자 ID 추출
            if (adminid == null || adminid.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 JWT 토큰입니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 토큰 파싱 중 오류가 발생했습니다.");
        }

        // adminid로 adminidx 변환
        Integer adminidx = masterService.getAdminIdxByAdminid(adminid);
        if (adminidx == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 정보를 찾을 수 없습니다.");
        }

        // 파일 저장 로직 (예시)
        String thumfileName = null;
        try {
            if (thumfile != null && !thumfile.isEmpty()) {
                // 파일 저장 메서드 (파일명 반환)
                thumfileName = uploadFileToExternalServer(thumfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생했습니다.");
        }

        // 이벤트 엔티티 생성 및 데이터 설정
        MasterVO event = new MasterVO();
        event.setTitle(title);
        event.setContent(content);
        event.setEvent_date(event_date);
        event.setThumfile(thumfileName); // 파일명 설정
        event.setAdminidx(adminidx);

        try {
            // 이벤트 추가 서비스 호출
            masterService.addEvent(event);
            return ResponseEntity.ok("이벤트가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이벤트 등록 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/getEventDetail")
    @ResponseBody
    public MasterVO getEventDetail(@RequestParam("idx") int idx) {
        // 이벤트 상세 정보를 가져오기 위한 서비스 호출
        MasterVO eventDetail = masterService.getEventDetail(idx);
        return eventDetail;
    }

    @PostMapping("/EventEditMasterOk")
    public ResponseEntity<String> EventEditMasterOk(
            @RequestParam(value = "idx", required = false, defaultValue = "0") int idx,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("event_date") String event_date,
            @RequestParam("thumfile") MultipartFile thumfile,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        // Authorization 헤더 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }

        // JWT 토큰에서 관리자 ID 추출
        String token = authorizationHeader.substring(7);  // "Bearer " 부분을 제거
        String adminid;
        try {
            adminid = jwtUtil.getUserIdFromToken(token); // JWT에서 관리자 ID 추출
            if (adminid == null || adminid.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 JWT 토큰입니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 토큰 파싱 중 오류가 발생했습니다.");
        }

        // adminid로 adminidx 변환
        Integer adminidx = masterService.getAdminIdxByAdminid(adminid);
        if (adminidx == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 정보를 찾을 수 없습니다.");
        }

        // 파일 저장 처리
        String thumfileName = null;
        try {
            if (thumfile != null && !thumfile.isEmpty()) {
                // 파일 저장 메서드 호출
                thumfileName = uploadFileToExternalServer(thumfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생했습니다.");
        }

        // 이벤트 수정 엔티티 생성 및 데이터 설정
        MasterVO event = new MasterVO();
        event.setIdx(idx);
        event.setTitle(title);
        event.setContent(content);
        event.setEvent_date(event_date);
        event.setThumfile(thumfileName); // 파일명 설정
        event.setAdminidx(adminidx);

        try {
            // 이벤트 수정 서비스 호출
            boolean isUpdated = masterService.updateEvent(event);
            if (isUpdated) {
                return ResponseEntity.ok("이벤트가 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이벤트 수정 중 오류가 발생했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이벤트 수정 중 오류가 발생했습니다.");
        }
    }

    // 차트 보여주기
    @GetMapping("/registrationChart")
    public ResponseEntity<List<Map<String, Object>>> getRegistrationStats() {
        List<Map<String, Object>> stats = masterService.getUserRegistrationStats();
        return ResponseEntity.ok(stats);
    }
}
