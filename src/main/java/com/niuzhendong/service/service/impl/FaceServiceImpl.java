package com.niuzhendong.service.service.impl;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.ImageQuality;
import com.arcsoft.face.toolkit.ImageInfo;
import com.niuzhendong.service.dao.FaceDao;
import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.dto.FeatureItem;
import com.niuzhendong.service.service.FaceService;
import com.niuzhendong.service.utils.FaceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaceServiceImpl implements FaceService {

    @Autowired
    private FaceUtils faceUtils;

    @Autowired
    private FaceDao faceDao;

    @Override
    public List<FaceInfo> faceExists(ImageInfo imageInfo) {
        return faceUtils.faceExists(imageInfo);
    }

    @Override
    public List<Face> getFaceInfo(List<Long> ids) {
        return faceDao.getFaceInfo(ids);
    }

    @Override
    public List<FeatureItem> imageQualityDetect(ImageInfo imageInfo, int mask, List<FaceInfo> faceInfos) {
        return faceUtils.imageQualityDetect(imageInfo,mask,faceInfos);
    }

    @Override
    public FeatureItem imageQualityDetectForSingle(ImageInfo imageInfo, int mask, FaceInfo faceInfo) {
        return faceUtils.imageQualityDetectForSingle(imageInfo,mask,faceInfo);
    }

    @Override
    public FaceFeature getImageFeature(ImageInfo imageInfo, FaceInfo faceInfo) {
        return faceUtils.getImageFeature(imageInfo,faceInfo);
    }

    @Override
    public FaceSimilar compareFaceFeature(FaceFeature targetFaceFeature, FaceFeature sourceFaceFeature) {
        return faceUtils.compareFaceFeature(targetFaceFeature,sourceFaceFeature);
    }

    @Override
    public void updateFaceStatus(List<Long> ids) {
        faceDao.updateFaceStatus(ids);
    }


}
