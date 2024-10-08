package com.ict.finalproject.DAO;

import com.ict.finalproject.vo.StoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StoreDAO {
    public List<StoreVO> getStoreList();

    public List<StoreVO> getStoreListByFilter(@Param("filterType")String filterType);

    public List<StoreVO> searchStoreList(@Param("keyword") String keyword);

    public List<StoreVO> getStoreListByFilterCriteria(@Param("brand") String brand, @Param("ani_title") String ani_title);
}
