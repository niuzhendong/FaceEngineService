package com.niuzhendong.service.dao;

import com.niuzhendong.service.dto.Face;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface  FaceDao {
    List<Face> getFaceInfo();
    List<Face> getFaceList();
    List<Face> findFaceInfoForList(String query);
    void updateFaceStatus(List<Long> ids);
    void updateFace(Face face);
    List<Face> findFaceInfo(List<Long> ids);
}
