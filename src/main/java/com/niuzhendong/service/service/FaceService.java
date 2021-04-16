package com.niuzhendong.service.service;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.ImageQuality;
import com.arcsoft.face.toolkit.ImageInfo;
import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.dto.FeatureItem;

import java.io.InputStream;
import java.util.List;

public interface FaceService {

    List<FaceInfo> faceExists(ImageInfo imageInfo);

    List<Face> getFaceInfo(List<Long> ids);

    List<FeatureItem> imageQualityDetect(ImageInfo imageInfo, int mask, List<FaceInfo> faceInfos);

    FeatureItem imageQualityDetectForSingle(ImageInfo imageInfo, int mask, FaceInfo faceInfo);

    FaceFeature getImageFeature(ImageInfo imageInfo, FaceInfo faceInfo);

    FaceSimilar compareFaceFeature(FaceFeature targetFaceFeature, FaceFeature sourceFaceFeature);

    void updateFaceStatus(List<Long> ids);
}
