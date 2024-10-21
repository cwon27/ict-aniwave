package com.ict.finalproject.Service;

import com.ict.finalproject.vo.MasterVO;
import com.ict.finalproject.vo.StoreVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public interface MasterService {
    MasterVO createNotice(MasterVO notice);
    MasterVO createFAQ(MasterVO faq);
    int createStore(MasterVO storeAdd);
    public List<MasterVO> getFAQList();
    public List<MasterVO> getStoreList();
     int getTotalStore();
    Map<String, Object> getCategoryCountByCode(@Param("categoryCode") int categoryCode);
    List<MasterVO> getBoardList();
    Integer getAdminIdxByAdminid(String adminid);
    public List<MasterVO> getAniAllList();
    List<MasterVO> getNoticeList();
    List<MasterVO> getReviewList();
    void addReport(String userid, String reason, LocalDateTime stopDT, LocalDateTime endDT);
    boolean checkUserBanStatus(String userid);
    boolean checkUserDelected(int idx);
    Integer findUserIdxByUserid(String userid);
    List<MasterVO> getMemberDelList(MasterVO vo);
    List<MasterVO> getReportinguserList(MasterVO vo);
    void updateReportAndBan(int idx,String userid, String reason, LocalDateTime stopDT, LocalDateTime handleDT, LocalDateTime endDT, int handleState);
    List<MasterVO> getReplyList(MasterVO vo);
    MasterVO getReviewDetail(int idx);
    boolean updateAnimation(MasterVO vo);
    MasterVO aniSelect(int idx);
    String getCurrentImgFile(int idx);
    List<MasterVO> getQNAList();
    int getUnansweredQnaCount();
    List<MasterVO>getReportingUser();
    int getTotalReportCount();
    int getTotalUserReport(int useridx);
    void updateQnaAndReply(int idx, String reply, int adminIdx);
    Integer findAdminIdxByUserid(String adminId);
    void addAnimation(MasterVO aniVO);
    void deletePostByIdx(int idx);
    MasterVO getStoreByIdx(int idx);
    boolean updateStore(MasterVO store);
    List<MasterVO> getSubCategoriesByCategory(int category);
    void insertProductImg(MasterVO masterVO);
    MasterVO getNoticeById(int idx);
    boolean updateNotice(MasterVO noticeEdit);
    MasterVO noticeSelect (int idx);
    MasterVO getQnaById(int idx);
    boolean validateAdmin(String adminid, String adminpwd);
    boolean doesProductExist(int pro_idx);
    MasterVO getProductImgByIdx(int idx);
    boolean updateProductImg(MasterVO productImg);
    int getTotalAnimeCount(); // 총 애니메이션 수를 가져옴
    List<MasterVO> getAniListWithPaging(int currentPage, int pageSize); // 페이징된 애니 리스트 가져오기
    List<MasterVO> getStoreListWithPaging(int currentPage, int pageSize);
    List<MasterVO> getBoardListWithPaging(int currentPage, int pageSize);
    int getTotalBoardCount();
    List<MasterVO> getReviewListWithPaging(int offset, int pageSize);
    int getTotalReviewCount();
    void addEvent(MasterVO event);
    List<MasterVO> getEventList();
    public MasterVO getEventDetail(int idx);
    boolean updateEvent(MasterVO event);
    MasterVO getEventByIdx(int idx);
    List<Map<String, Object>> getUserRegistrationStats();
    void deleteStoreByIdx(int idx);
    void deleteProductImagesByProductIdx(int idx);
    List<MasterVO> getReportingUserWithPaging(int offset, int pageSize);
    int getTotalReportingUserCount();
    List<MasterVO> getReplyListWithPaging(int offset, int pageSize);
    int getTotalReplyCount();
    public MasterVO getCommentByIdx(int idx);
    public List<MasterVO> getRepliesByCommentIdx(int idx);
    boolean authenticateAdmin(String adminid, String adminpwd);
    List<MasterVO> getUserListWithPaging(int offset, int pageSize);
    MasterVO getFAQById(int idx);
    void updateFAQ(MasterVO faq);
    void deleteReport(int idx);
}
