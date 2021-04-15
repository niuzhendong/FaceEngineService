package com.niuzhendong.service.service.impl;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.ImageQuality;
import com.niuzhendong.service.dao.FaceDao;
import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.dto.FeatureItem;
import com.niuzhendong.service.service.FaceService;
import com.niuzhendong.service.utils.FaceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class FaceServiceImpl implements FaceService {

    @Autowired
    private FaceUtils faceUtils;

    @Autowired
    private FaceDao faceDao;

    @Override
    public List<FaceInfo> faceExists(InputStream inputStream) {
        return faceUtils.faceExists(inputStream);
    }

    @Override
    public List<Face> getFaceInfo(List<Long> ids) {
        return faceDao.getFaceInfo(ids);
    }

    @Override
    public List<FeatureItem> imageQualityDetect(InputStream inputStream, int mask, List<FaceInfo> faceInfos) {
        return faceUtils.imageQualityDetect(inputStream,mask,faceInfos);
    }

    @Override
    public FeatureItem imageQualityDetectForSingle(InputStream inputStream, int mask, FaceInfo faceInfo) {
        return faceUtils.imageQualityDetectForSingle(inputStream,mask,faceInfo);
    }

    @Override
    public FaceFeature getImageFeatureForRegister(InputStream inputStream) {
        return faceUtils.getImageFeatureForRegister(inputStream);
    }

    @Override
    public FaceFeature getImageFeatureForRecognize(InputStream inputStream, FaceInfo faceInfo) {
        return faceUtils.getImageFeatureForRecognize(inputStream,faceInfo);
    }

    @Override
    public FaceSimilar compareFaceFeature(FaceFeature targetFaceFeature, FaceFeature sourceFaceFeature) {
        return faceUtils.compareFaceFeature(targetFaceFeature,sourceFaceFeature);
    }


}
