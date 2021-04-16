package com.niuzhendong.service.utils;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.CompareModel;
import com.arcsoft.face.enums.ExtractType;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.niuzhendong.service.dto.FeatureItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class FaceUtils {

    Logger logger = LoggerFactory.getLogger(FaceUtils.class);

    @Autowired
    private FaceEngine faceEngine;

    /**
     * 人脸检测
     * @param imageInfo
     * @return
     */
    public List<FaceInfo> faceExists(ImageInfo imageInfo){
        int errorCode;
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        errorCode = faceEngine.detectFaces(imageInfo, faceInfoList);
        logger.info("人脸引擎处理结果状态："+errorCode);
        return faceInfoList;
    }

    /**
     * 图片质量检测
     * @param imageInfo
     * @param mask 遮盖信息
     * @return 返回图片中的所有人脸图像质量信息
     */
    public List<FeatureItem> imageQualityDetect(ImageInfo imageInfo, int mask, List<FaceInfo> faceInfos){
        int errorCode;
        //定义内部流转结构
        List<FeatureItem> featureItems = new ArrayList<>();
        //对传入的人脸结构逐一检测
        for (FaceInfo faceInfo:faceInfos){
            FeatureItem featureItem = new FeatureItem();
            ImageQuality imageQuality = new ImageQuality();
            errorCode = faceEngine.imageQualityDetect(imageInfo, faceInfo,mask, imageQuality);
            logger.info("人脸引擎处理结果状态：图像质量检测----"+errorCode);
            //封装，将人脸结构信息及图像质量一一对应
            featureItem.setFaceInfo(faceInfo);
            featureItem.setImageQuality(imageQuality);
            featureItems.add(featureItem);
        }
        return featureItems;
    }

    /**
     * 图片质量检测
     * @param imageInfo
     * @param mask 遮盖信息
     * @return 返回图片中的所有人脸图像质量信息
     */
    public FeatureItem imageQualityDetectForSingle(ImageInfo imageInfo, int mask, FaceInfo faceInfo){
        int errorCode;
        //对传入的人脸结构检测
        ImageQuality imageQuality = new ImageQuality();
        errorCode = faceEngine.imageQualityDetect(imageInfo, faceInfo,mask, imageQuality);
        logger.info("人脸引擎处理结果状态：图像质量检测----"+errorCode);
        //封装，将人脸结构信息及图像质量一一对应
        FeatureItem featureItem = new FeatureItem();
        featureItem.setFaceInfo(faceInfo);
        featureItem.setImageQuality(imageQuality);
        return featureItem;
    }

    /**
     * 获取图像人脸特征-单人脸注册获取特征（确保图象中只有一个人时使用）
     * @param imageInfo
     * @return
     */
    public FaceFeature getImageFeature(ImageInfo imageInfo, FaceInfo faceInfo){

        //sdk指名需要图像数据和人脸数据同事传入
        //TODO 测试仅使用FaceInfo中的人脸数据进行单一来源处理是否可行
        int errorCode;
        //因使用jni，人脸特征需要创建后传入被改写，注意不能多次或重复识别，会覆盖上一次特征
        FaceFeature faceFeature = new FaceFeature();
        errorCode = faceEngine.extractFaceFeature(imageInfo,faceInfo, ExtractType.RECOGNIZE,0,faceFeature);
        logger.info("人脸引擎处理结果状态："+errorCode);

        return faceFeature.clone();
    }

    /**
     * 比对人脸特征
     * @param targetFaceFeature
     * @param sourceFaceFeature
     * @return
     */
    public FaceSimilar  compareFaceFeature(FaceFeature targetFaceFeature, FaceFeature sourceFaceFeature){
        int errorCode;
        FaceSimilar faceSimilar = new FaceSimilar();
        errorCode=faceEngine.compareFaceFeature(targetFaceFeature,sourceFaceFeature, CompareModel.ID_PHOTO,faceSimilar);
        logger.info("人脸引擎处理结果状态："+errorCode);
        return faceSimilar;
    }
}
