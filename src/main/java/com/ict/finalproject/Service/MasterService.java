package com.ict.finalproject.Service;

import com.ict.finalproject.DTO.*;
import com.ict.finalproject.vo.MasterVO;
import com.ict.finalproject.vo.OrderListVO;
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

    // 채원 시작
    // 주문 상품 데이터 불러오기
    List<CurrentOrderDataDTO> getUserOrderList(int page, int pageSize,String startDate, String endDate, String searchType, String searchKeyword);
    int getTotalOrderListCount(String startDate, String endDate, String searchType, String searchKeyword);
    OrderStateCountDTO getStateCount();
    // 주문 상세정보 데이터
    OrderListDTO getDetailInfo(int order_idx);
    List<OrderListVO> getDetailProducts(int order_idx);
    // 운송장 번호 저장
    int saveTrackingNumber(int order_idx, String trackingNum);
    // 배송시작으로 상태 변경
    void updatedeliOrderState(int order_idx, int state);
    // 주문 상태 변경
    int updateOrderState(int idx, int orderState);
    // 매출 내역
    List<SalesListDTO> getSalesList(int page, int pageSize, String startDate, String endDate);
    int getTotalSalesListCount(String startDate, String endDate);
}
