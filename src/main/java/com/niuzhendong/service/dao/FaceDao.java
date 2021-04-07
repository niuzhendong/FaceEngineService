package com.niuzhendong.service.dao;

import com.niuzhendong.service.dto.Face;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface  FaceDao {

    @Select("SELECT * FROM CITY WHERE state = #{state}")
    Face findByState(@Param("state") String state);


}
