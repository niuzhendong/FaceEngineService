package com.niuzhendong.service.utils;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.ImageQuality;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.arcsoft.face.toolkit.ImageInfoEx;
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
     * @param inputStream
     * @return
     */
    private List<FaceInfo> faceExists(InputStream inputStream){
        int errorCode;
        ImageInfo imageInfo = ImageFactory.getRGBData(inputStream);
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        errorCode = faceEngine.detectFaces(imageInfo, faceInfoList);
        logger.info("人脸引擎处理结果状态："+errorCode);
        return faceInfoList;
    }

    /**
     * 图片质量检测
     * @param imageInfo
     * @return
     */
    private ImageQuality imageQualityDetect(ImageInfoEx imageInfo){

        int errorCode;

        ImageQuality imageQuality = new ImageQuality();

        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();

        errorCode = faceEngine.imageQualityDetect(imageInfo, faceInfoList.get(0),0, imageQuality);

        return imageQuality;
    }
}
