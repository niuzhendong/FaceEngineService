package com.niuzhendong.service.config;

import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FaceConfig {

    Logger logger = LoggerFactory.getLogger(FaceConfig.class);

    @Autowired
    private FaceProp faceProp;

    /**
     * 获取FaceEngine
     */
    @Bean
    public FaceEngine FaceEngine() {
        int errorCode = 0;
        String libPath = "D:\\git\\FaceEngineService\\libs\\WIN64";
        //String libPath = "/home/niuzhendong/git/service/libs/LINUX64";
        /**
         * try {
         *             libPath = ResourceUtils.getURL("classpath:").getPath()+"WIN64";
         *         } catch (FileNotFoundException e) {
         *             e.printStackTrace();
         *         }
         */
        FaceEngine faceEngine = new FaceEngine(libPath);
        if (faceProp.getActiveFile().equals(null) || faceProp.getActiveFile().equals("")) {
            errorCode = faceEngine.activeOnline(faceProp.getAppId(),faceProp.getSdkKey(),faceProp.getActiveKey());
            logger.info("人脸引擎处理结果状态："+errorCode);
        } else {
            errorCode = faceEngine.activeOffline(faceProp.getActiveFile());
            logger.info("人脸引擎处理结果状态："+errorCode);
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
        errorCode = faceEngine.init(engineConfiguration);
        logger.info("人脸引擎处理结果状态："+errorCode);
        return faceEngine;
    }

}
