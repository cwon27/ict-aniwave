package com.ict.finalproject.Service;

import com.ict.finalproject.DAO.MasterDAO;
import com.ict.finalproject.vo.MasterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MasterServiceImpl implements MasterService {
    @Autowired
    MasterDAO dao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    // 관리자페이지 공지사항 추가
    public MasterVO createNotice(MasterVO notice) {
        /**
         * 관리자 페이지에서 공지사항을 추가하는 메서드입니다.
         *
         * @param vo 공지사항의 정보를 담고 있는 MasterVO 객체 (제목, 내용, 작성자 정보 포함)
         * @return 데이터베이스에 공지사항을 성공적으로 추가한 경우 1, 실패한 경우 0 반환
         */
        int result = dao.insertNotice(notice);
        return result > 0 ? notice : null;
    }

    @Override
    public MasterVO createFAQ(MasterVO faq) {
        int result = dao.insertFAQ(faq);
        return result > 0 ? faq : null;
    }

    @Override
    @Transactional
    public int createStore(MasterVO storeAdd) {
         dao.createStore(storeAdd);
         return storeAdd.getIdx();
    }

    @Override
    public List<MasterVO> getFAQList() {
        return dao.getFAQList();
    }

    @Override
    public List<MasterVO> getStoreList() {
        return dao.getStoreList();
    }

    @Override
    public int getTotalStore() {
        return dao.getTotalStore();
    }

    @Override
    public Map<String, Object> getCategoryCountByCode(int categoryCode) {
        return dao.getCategoryCountByCode(categoryCode);
    }

    @Override
    public List<MasterVO> getBoardList() {
        return dao.getBoardList();
    }

    @Override
    public Integer getAdminIdxByAdminid(String adminid) {
        return dao.getAdminIdxByAdminid(adminid);
    }

    @Override
    public List<MasterVO> getAniAllList() {
        return dao.getAniAllList();
    }

    @Override
    public List<MasterVO> getNoticeList() {
        return dao.getNoticeList();
    }

    @Override
    public List<MasterVO> getReviewList() {
        return dao.getReviewList();
    }

    @Override
    public void addReport(String userid, String reason, LocalDateTime stopDT, LocalDateTime endDT) {
        dao.insertReport(userid, reason, stopDT, endDT);
    }
    @Override
    public boolean checkUserBanStatus(String userid) {
        return dao.isUserBanned(userid);
    }

    @Override
    public boolean checkUserDelected(int idx) {
        return dao.isUserDeleted(idx);
    }

    @Override
    public Integer findUserIdxByUserid(String userid) {
        return dao.findUserIdxByUserid(userid);
    }

    @Override
    public List<MasterVO> getMemberDelList(MasterVO vo) {
        return dao.getMemberDelList(vo);
    }

    @Override
    public List<MasterVO> getReportinguserList(MasterVO vo) {
        return dao.getReportinguserList(vo);
    }

    @Override
    public void updateReportAndBan(int idx,String userid, String reason, LocalDateTime stopDT, LocalDateTime handleDT, LocalDateTime endDT, int handleState) {
        dao.updateReport(idx,userid, reason, LocalDateTime.now(),handleDT, handleState);
        dao.insertReport(userid, reason, stopDT, endDT);
    }

    @Override
    public List<MasterVO> getReplyList(MasterVO vo) {
        return dao.getReplyList(vo);
    }

    @Override
    public MasterVO getReviewDetail(int idx) {
        return dao.getReviewDetail(idx);
    }

    @Override
    public boolean updateAnimation(MasterVO vo) {
        int result = dao.updateAnimation(vo);
        return result > 0;
    }

    @Override
    public MasterVO aniSelect(int idx) {
        return dao.aniSelect(idx);
    }

    @Override
    public String getCurrentImgFile(int idx) {
        return dao.getCurrentImgFile(idx);
    }

    @Override
    public List<MasterVO> getQNAList() {
        return dao.getQNAList();
    }

    @Override
    public int getUnansweredQnaCount() {
        return dao.getUnansweredQnaCount();
    }

    @Override
    public List<MasterVO> getReportingUser() {
        return dao.getReportingUser();
    }

    @Override
    public int getTotalReportCount() {
        return dao.getTotalReportCount();
    }

    @Override
    public int getTotalUserReport(int useridx) {
        return dao.getTotalUserReport(useridx);
    }

    @Override
    public void updateQnaAndReply(int idx, String reply, int adminIdx) {
        dao.updateHandleState(idx);
        dao.insertReply(idx, reply, adminIdx, LocalDateTime.now());
    }

    @Override
    public Integer findAdminIdxByUserid(String adminId) {
        return dao.findAdminIdxByUserid(adminId);
    }

    @Override
    public void addAnimation(MasterVO aniVO) {
        dao.insertAnimation(aniVO);
    }

    @Override
    public void deletePostByIdx(int idx) {
        dao.deletePostByIdx(idx);
    }

    @Override
    public MasterVO getStoreByIdx(int idx) {
        return dao.getStoreByIdx(idx);
    }

    @Override
    public boolean updateStore(MasterVO store) {
        int result = dao.updateStore(store);
        return result > 0;
    }

    @Override
    public List<MasterVO> getSubCategoriesByCategory(int category) {
        return dao.findSubCategoriesByCategory(category);
    }

    @Override
    public void insertProductImg(MasterVO masterVO) {
        dao.insertProductImg(masterVO);
    }


    @Override
    public MasterVO getNoticeById(int idx) {
        return dao.getNoticeById(idx);
    }

    @Override
    public boolean updateNotice(MasterVO noticeEdit) {
        return dao.updateNotice(noticeEdit) > 0;
    }

    @Override
    public MasterVO noticeSelect(int idx) {
        return dao.noticeSelect(idx);
    }

    @Override
    public MasterVO getQnaById(int idx) {
        return dao.getQnaReplyById(idx);
    }

    @Override
    public boolean validateAdmin(String adminid, String adminpwd) {
        MasterVO admin = dao.getAdminByAdminId(adminid);

        if (admin != null) {
            log.info("Database password (hashed): " + admin.getAdminpwd()); // 해시된 비밀번호 로그
            log.info("User input password (plain): " + adminpwd); // 사용자가 입력한 비밀번호 로그

            try {
                // 비밀번호 비교
                boolean matches = passwordEncoder.matches(adminpwd, admin.getAdminpwd());
                log.info("Password match result: " + matches);
                return matches; // 비밀번호가 일치하는지 반환
            } catch (Exception e) {
                log.error("Error occurred during password comparison", e);
                throw new RuntimeException("Password comparison failed");
            }
        } else {
            log.info("Admin not found with id: " + adminid);
            return false;
        }
    }

    @Override
    public boolean doesProductExist(int pro_idx) {
        int count = dao.doesProductExist(pro_idx);
        return count > 0;
    }

    @Override
    public MasterVO getProductImgByIdx(int idx) {
        return dao.getProductImgByIdx(idx);
    }

    @Override
    public boolean updateProductImg(MasterVO productImg) {
        int result = dao.updateProductImg(productImg);
        return result > 0;
    }

    @Override
    public int getTotalAnimeCount() {
        return dao.getTotalAnimeCount();
    }

    @Override
    public List<MasterVO> getAniListWithPaging(int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", limit);
        return dao.getAniListWithPaging(params);
    }

    @Override
    public List<MasterVO> getStoreListWithPaging(int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", limit);
        return dao.getStoreListWithPaging(params);
    }

    @Override
    public List<MasterVO> getBoardListWithPaging(int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", limit);
        return dao.getBoardListWithPaging(params);
    }

    @Override
    public int getTotalBoardCount() {
        return dao.getTotalBoardCount();
    }

    @Override
    public List<MasterVO> getReviewListWithPaging(int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", limit);
        return dao.getReviewListWithPaging(params);
    }

    @Override
    public int getTotalReviewCount() {
        return dao.getTotalReviewCount();
    }

    @Override
    public void addEvent(MasterVO event) {
        int result = dao.insertEvent(event);
        if (result > 0) {
            log.info("이벤트 등록 성공. Event idx: {}", result);
        } else {
            log.error("이벤트 등록 실패");
        }
    }

    @Override
    public List<MasterVO> getEventList() {
        return dao.getEventList();
    }

    @Override
    public MasterVO getEventDetail(int idx) {
        return dao.getEventDetail(idx);
    }

    @Override
    public boolean updateEvent(MasterVO event) {
        return dao.updateEvent(event) > 0;
    }

    @Override
    public MasterVO getEventByIdx(int idx) {
        return dao.getEventByIdx(idx);
    }

    @Override
    public List<Map<String, Object>> getUserRegistrationStats() {
        return dao.getUserRegistrationStats();
    }

    @Override
    public void deleteStoreByIdx(int idx) {
        dao.deleteStoreByIdx(idx);
    }

    @Override
    public void deleteProductImagesByProductIdx(int idx) {
        dao.deleteProductImagesByProductIdx(idx);
    }

    @Override
    public List<MasterVO> getReportingUserWithPaging(int offset, int pageSize) {
        return dao.getReportingUserWithPaging(offset, pageSize);
    }

    @Override
    public int getTotalReportingUserCount() {
        return dao.getTotalReportingUserCount();
    }

    @Override
    public List<MasterVO> getReplyListWithPaging(int offset, int pageSize) {
        return dao.getReplyListWithPaging(offset, pageSize);
    }

    @Override
    public int getTotalReplyCount() {
        return dao.getTotalReplyCount();
    }
}
