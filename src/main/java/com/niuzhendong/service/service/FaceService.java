package com.niuzhendong.service.service;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.ImageQuality;
import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.dto.FeatureItem;

import java.io.InputStream;
import java.util.List;

public interface FaceService {

    List<FaceInfo> faceExists(InputStream inputStream);

    List<Face> getFaceInfo(List<Long> ids);

    List<FeatureItem> imageQualityDetect(InputStream inputStream, int mask, List<FaceInfo> faceInfos);

    FaceFeature getImageFeatureForRegister(InputStream inputStream);

    FaceFeature getImageFeatureForRecognize(InputStream inputStream, FaceInfo faceInfo);

    FaceSimilar compareFaceFeature(FaceFeature targetFaceFeature, FaceFeature sourceFaceFeature);
}
