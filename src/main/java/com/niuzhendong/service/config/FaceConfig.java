package com.niuzhendong.service.config;

import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FaceConfig {

    @Autowired
    private FaceProp faceProp;

    /**
     * 获取FaceEngine
     */
    @Bean
    public FaceEngine FaceEngine() {
        FaceEngine faceEngine = new FaceEngine();
        if (faceProp.getActiveFile().equals(null) || faceProp.getActiveFile().equals("")) {
            faceEngine.activeOnline(faceProp.getAppId(),faceProp.getSdkKey(),faceProp.getActiveKey());
        } else {
            faceEngine.activeOffline(faceProp.getActiveFile());
        }
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);
        engineConfiguration.setDetectFaceMaxNum(10);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        functionConfiguration.setSupportImageQuality(true);
        functionConfiguration.setSupportMaskDetect(true);
        functionConfiguration.setSupportFaceLandmark(true);
        functionConfiguration.setSupportUpdateFaceData(true);
        functionConfiguration.setSupportFaceShelter(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        //初始化引擎
        faceEngine.init(engineConfiguration);
        return faceEngine;
    }

}
