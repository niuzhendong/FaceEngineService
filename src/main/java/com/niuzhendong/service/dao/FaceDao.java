package com.niuzhendong.service.dao;

import com.niuzhendong.service.dto.Face;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface  FaceDao {
    List<Face> getFaceInfo(List<Long> id);
    List<Face> findFaceInfo(Face face);
}
