package com.ict.finalproject.DAO;

import com.ict.finalproject.DTO.BasketDTO;
import com.ict.finalproject.vo.BasketVO;
import com.ict.finalproject.vo.ProductFilterVO;
import com.ict.finalproject.vo.StoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StoreDAO {
    //최근 상품 목록 가져오기
    public List<StoreVO> getRecentProducts();
    //전체 상품 목록 가져오기
    public List<StoreVO> getStoreList();
    //총 상품 목록 가져오기
    public int getTotalProductCount();
    //페이지네이션 처리를 위한 상품목록 가져오기
    public List<StoreVO> getPagedProducts(@Param("pageSize") int pageSize, @Param("offset") int offset, @Param("category") Integer category,@Param("second_category") Integer second_category); //페이지네이션
    // 필터 타입에 따른 상품 리스트 가져오기 (필터 타입: 최신순, 인기순 등)
    public List<StoreVO> getStoreListByFilter(@Param("filterType")String filterType);
    // 검색어에 따른 상품 검색
    public List<StoreVO> searchStoreList(@Param("keyword") String keyword);
    // 첫 번째 카테고리 목록 가져오기
    public List<ProductFilterVO> getFirstCategoryList();  // 이 부분 추가
    // 상품 상세 정보 가져오기
    public StoreVO getStoreDetail(int storeId);
    // 카테고리 코드에 따른 하위 카테고리 목록 가져오기
    public List<String> getSubcategoriesByFirstCategory(@Param("code") int code);
    // 추가된 부분: 카테고리별 상품 목록 가져오기
    public List<StoreVO> getProductsByCategory(@Param("pageSize") int pageSize, @Param("offset") int offset, @Param("category") int category);

    public List<StoreVO> getStoreWithPopularity(Map<String, Object> params);
    
    //채원
    //장바구니에 상품 있는지 체크
    int checkProductInBasket(BasketVO basketvo);
    //장바구니 테이블에 상품데이터 저장
    int basketInput(BasketVO basketvo);
    //장바구니 리스트
    List<BasketDTO> basketList(int useridx);
    //장바구니 상품 삭제(x버튼)
    int basketDelete(int idx,int useridx);
    //장바구니 상품 삭제(선택,전체상품삭제)
    void basketChoiceAndAllDelOk(int idx, int useridx);
    //장바구니 상품갯수 +
    int basketPlusAmount(int idx,int useridx, int newTotal);
    //장바구니 상품갯수 -
    int basketMinusAmount(int idx,int useridx, int newTotal);
}
