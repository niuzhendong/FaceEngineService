package com.niuzhendong.service.dto;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.ImageQuality;
import lombok.Data;

/**
 * 内部数据交换类型，记录某一个人脸的，人脸结构信息、人脸特征及图像质量
 */
@Data
public class FeatureItem {
    private FaceFeature feature;
    private ImageQuality imageQuality;
    private FaceInfo faceInfo;
}
