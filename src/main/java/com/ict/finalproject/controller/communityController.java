package com.ict.finalproject.controller;

import com.ict.finalproject.JWT.JWTUtil;
import com.ict.finalproject.Service.CommuService;
import com.ict.finalproject.Service.MemberService;
import com.ict.finalproject.vo.CommuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.List;


//@RequestMapping("/community")
@Controller
public class communityController {
    // userid로 index구하기
    @Autowired
    MemberService mservice;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    private CommuService commuService;


//    ModelAndView mav = null;
    // 커뮤니티 리스트 페이지
    @GetMapping("/cmList")
    public String cmList(@RequestParam(value = "commtype", required = false, defaultValue = "all") String commtype, Model model) {
        System.out.println("Received commtype: " + commtype); // 전달받은 commtype 값 확인
        List<CommuVO> list;
        if ("all".equals(commtype)) {
            list = commuService.List(null); // 전체 목록 조회
        } else {
            list = commuService.List(commtype); // 특정 commtype 목록 조회
        }
        System.out.println("Filtered List: " + list); // 필터링된 목록 출력
        model.addAttribute("list", list);
        model.addAttribute("commtype", commtype); // 현재 선택된 커뮤니티 타입 전달
        return "community/cmList";
    }


    //로그인 여부
    @ResponseBody
    @GetMapping("/getuser")
    public String getuser(@RequestParam("Authorization")String token){
        System.out.println("hi2");
        String userid = jwtUtil.getUserid(token); //토큰에서 사용자 아이디 추출
        System.out.println("userid : " + userid);
        return userid;
    }

    //글등록폼
    @GetMapping("/cmWrite")
    public String cmwrite(){

//        mav = new ModelAndView();
//        mav.setViewName("community/cmWrite");
        return "community/cmWrite";
    }

    //글 등록(DB)


    @PostMapping("/cmWriteOk")
    public ResponseEntity<String> writeOk(
            @RequestParam("code") String code, // communitytype 테이블의 code 필드와 매핑
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("token") String token
    ) {
        //토큰에서 사용자 아이디 추출
        String userid = jwtUtil.getUserid(token);
        // userid로 index구하기
        int useridx= mservice.getUseridx(userid);

        // 게시글 VO 객체 생성 및 설정
        CommuVO board = new CommuVO();
        board.setUseridx(useridx); // 사용자 ID 설정
        board.setCommtype(code); // communitytype의 code 필드 설정
        board.setTitle(title); // 제목 설정
        board.setContent(content); // 내용 설정
        //참고로 이런 비즈니스 로직은 service단에서 하고 컨트롤러에서는 그냥 서비스에 전달해주는 게 권장되는 방식
        String bodyTag = "";
        try {
            CommuVO resultBoard = commuService.writeBoard(board); // 게시글 등록 서비스 호출

            if (resultBoard == null) { // 등록 실패 시
                bodyTag += "<script>alert('등록 실패');history.back();</script>";
            } else { // 등록 성공 시
                // 자바스크립트로 페이지 이동 처리
                bodyTag += "<script>alert('등록 성공'); location.href='/cmList';</script>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            bodyTag += "<script>alert('등록 실패');history.back();</script>";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8")));

        return new ResponseEntity<>(bodyTag, headers, HttpStatus.OK);
    }



    //상세페이지
    @GetMapping("/cmView")
    public String cmView(){
        return "community/cmView";
    }

    //커뮤니티-공지사항 이동
    @GetMapping("/allnotice")
    public String allnotice(){
        return "notice/notice2";
    }
}