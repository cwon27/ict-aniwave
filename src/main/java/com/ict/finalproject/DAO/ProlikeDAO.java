package com.ict.finalproject.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ict.finalproject.vo.ProLikeVO;

@Mapper
public interface ProlikeDAO {

   public void insertLike(ProLikeVO likeVO);  // 좋아요 추가
   public void deleteLike(ProLikeVO likeVO);  // 좋아요 취소
   public ProLikeVO selectLikeStatus(int pro_idx,  int useridx) ;// 좋아요 상태 확
   int getLikeCount(@Param("pro_idx") int pro_idx);
}