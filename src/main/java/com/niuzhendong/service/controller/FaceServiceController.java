package com.niuzhendong.service.controller;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.ImageQuality;
import com.niuzhendong.service.dto.CompareInfo;
import com.niuzhendong.service.dto.FeatureItem;
import com.niuzhendong.service.service.FaceService;
import com.niuzhendong.service.service.MilvusService;
import com.niuzhendong.service.service.MinioService;
import com.niuzhendong.service.utils.Result;
import io.milvus.client.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


@Controller(value = "/faceService")
public class FaceServiceController {

    @Autowired
    private FaceService faceService;

    @Autowired
    private MilvusService milvusService;

    @Autowired
    private MinioService minioService;

    @RequestMapping(value = "/api/compareFaceFeature", method = RequestMethod.GET)
    public Result<CompareInfo> compareFaceFeature(@RequestParam String url){

        CompareInfo compareInfo = new CompareInfo();
        InputStream inputStream = minioService.getFileFromUrl(url);
        List<FaceInfo> faceInfos = faceService.faceExists(inputStream);
        if (faceInfos.size() < 1){
            return new Result<CompareInfo>().error(500,"图片中未检测出人脸，请检查图像");
        }
        //遮挡值为0,表示没有戴口罩的场景
        List<FeatureItem> featureItems = faceService.imageQualityDetect(inputStream,0,faceInfos);
        for (FeatureItem featureItem:featureItems){
            //根据文档阈值设置建议，仅对图像质量大于0.49的图像进行识别（没有口罩的场景）
            if (featureItem.getImageQuality().getFaceQuality() > 0.49) {
                FaceFeature faceFeature = faceService.getImageFeatureForRecognize(inputStream,featureItem.getFaceInfo());
                //检测结果封装到内部流转数据结构中
                featureItem.setFeature(faceFeature);
                //构造人脸体征查询队列、为保证查询关联性，单一人脸传入，多次查询
                List<ByteBuffer> byteBuffers = new ArrayList<>();
                byteBuffers.add(ByteBuffer.wrap(faceFeature.getFeatureData()));
                //topK,返回1个匹配项
                SearchResponse searchResponse = milvusService.searchFeature(byteBuffers,1);
                if (searchResponse.getResponse().ok()){

                    compareInfo.getUrl("");
                    compareInfo.setTag("");
                    compareInfo.setId("1");
                }
            } else {
                featureItems.remove(featureItem);
            }
        }
        return new Result<CompareInfo>().ok(compareInfo);
    }
}
