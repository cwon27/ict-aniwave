package com.ict.finalproject.Service;

import com.ict.finalproject.vo.MasterVO;
import org.springframework.stereotype.Service;

import com.ict.finalproject.vo.ProLikeVO;

import java.util.List;

@Service
public interface ProlikeService {

    public void addLike(ProLikeVO likeVO);  // 좋아요 추가
    public void removeLike(ProLikeVO likeVO);  // 좋아요 취소


    public boolean isLiked(int pro_idx, int useridx);  // 좋아요 상태 확인
    public int getLikeCount(int pro_idx);

}

