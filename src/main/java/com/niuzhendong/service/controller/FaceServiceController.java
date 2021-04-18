package com.niuzhendong.service.controller;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.niuzhendong.service.dto.CompareInfo;
import com.niuzhendong.service.dto.Face;
import com.niuzhendong.service.dto.FeatureItem;
import com.niuzhendong.service.service.FaceService;
import com.niuzhendong.service.service.MilvusService;
import com.niuzhendong.service.service.MinioService;
import com.niuzhendong.service.utils.Result;
import io.milvus.client.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


@RestController
public class FaceServiceController {

    @Autowired
    private FaceService faceService;

    @Autowired
    private MilvusService milvusService;

    @Autowired
    private MinioService minioService;

    @RequestMapping(value = "/api/compareFaceFeature", method = RequestMethod.GET)
    public Result<CompareInfo> compareFaceFeature(@RequestParam String url) throws UnsupportedEncodingException{

        CompareInfo compareInfo = new CompareInfo();
        InputStream inputStream = minioService.getFileFromUrl(encodeUrl(url));
        ImageInfo imageInfo = ImageFactory.getRGBData(inputStream);

        List<FaceInfo> faceInfos = faceService.faceExists(imageInfo);
        if (faceInfos.size() < 1){
            return new Result<CompareInfo>().error(500,"图片中未检测出人脸，请检查图像");
        }
        //遮挡值为0,表示没有戴口罩的场景
        List<FeatureItem> featureItems = faceService.imageQualityDetect(imageInfo,0,faceInfos);
        for (FeatureItem featureItem:featureItems){
            //根据文档阈值设置建议，仅对图像质量大于0.49的图像进行识别（没有口罩的场景）
            if (featureItem.getImageQuality().getFaceQuality() > 0.49) {
                FaceFeature faceFeature = faceService.getImageFeature(imageInfo,featureItem.getFaceInfo());
                //检测结果封装到内部流转数据结构中
                featureItem.setFeature(faceFeature);
                //构造人脸体征查询队列、为保证查询关联性，单一人脸传入，多次查询
                List<ByteBuffer> byteBuffers = new ArrayList<>();
                byteBuffers.add(ByteBuffer.wrap(faceFeature.getFeatureData()));
                //topK,返回1个匹配项
                SearchResponse searchResponse = milvusService.searchFeature(byteBuffers,1);
                if (searchResponse.getResponse().ok()){

                }
            } else {
                featureItems.remove(featureItem);
            }
        }
        return new Result<CompareInfo>().ok(compareInfo);
    }


    @RequestMapping(value = "/api/compareFaceFeatureForSingle", method = RequestMethod.GET)
    public Result<CompareInfo> compareFaceFeatureForSingle(@RequestParam String url) throws UnsupportedEncodingException {

        CompareInfo compareInfo = new CompareInfo();
        InputStream inputStream = minioService.getFileFromUrl(encodeUrl(url));
        if (inputStream == null){
            return new Result<CompareInfo>().error(500,"图像获取异常，请检查图像链接");
        }
        ImageInfo imageInfo = ImageFactory.getRGBData(inputStream);
        List<FaceInfo> faceInfos = faceService.faceExists(imageInfo);
        if (faceInfos.size() < 1){
            return new Result<CompareInfo>().error(500,"图片中未检测出人脸，请检查图像");
        }
        if(faceInfos.size() > 1){
            return new Result<CompareInfo>().error(500,"图片中包含多个人脸，请使用多项比对接口");
        }
        //遮挡值为0,表示没有戴口罩的场景
        FeatureItem featureItem = faceService.imageQualityDetectForSingle(imageInfo,0,faceInfos.get(0));
        //根据文档阈值设置建议，仅对图像质量大于0.49的图像进行识别（没有口罩的场景）
        if (featureItem.getImageQuality().getFaceQuality() > 0.49) {
            FaceFeature faceFeature = faceService.getImageFeature(imageInfo,featureItem.getFaceInfo());
            //检测结果封装到内部流转数据结构中
            featureItem.setFeature(faceFeature);
            //构造人脸体征查询队列、为保证查询关联性，单一人脸传入，多次查询
            List<ByteBuffer> byteBuffers = new ArrayList<>();
            byteBuffers.add(ByteBuffer.wrap(faceFeature.getFeatureData()));
            //topK,返回1个匹配项
            SearchResponse searchResponse = milvusService.searchFeature(byteBuffers,1);
            if (searchResponse.getResponse().ok()){
                if (searchResponse.getResultDistancesList().get(0).get(0) > 0.6){
                    List<Long> ids = searchResponse.getResultIdsList().get(0);
                    List<Face> faces = faceService.getFaceInfo(ids);

                    if (faces.size() >= 1){

                    }
                    //compareInfo.setTag();
                    compareInfo.setId(ids.get(0));
                }else {

                }
            }
        } else {
            return new Result<CompareInfo>().error(500,"图片质量低于检测最低值，请检查图像");
        }
        return new Result<CompareInfo>().ok(compareInfo);
    }

    @RequestMapping(value = "/api/initFaceDBFeature", method = RequestMethod.GET)
    public Result initFaceDBFeature(@RequestParam List<Long> ids) {
        List<FeatureItem> featureItems = new ArrayList<>();
        List<Face> faces = faceService.getFaceInfo(ids);
        for (Face face:faces){
            InputStream inputStream = minioService.getFileFromMinio(face.getBucketName(),face.getFileName());
            ImageInfo imageInfo = ImageFactory.getRGBData(inputStream);
            List<FaceInfo> faceInfos = faceService.faceExists(imageInfo);
            //遮挡值为0,表示没有戴口罩的场景
            FeatureItem featureItem = faceService.imageQualityDetectForSingle(imageInfo,0,faceInfos.get(0));
            FaceFeature faceFeature = faceService.getImageFeature(imageInfo,featureItem.getFaceInfo());
            featureItem.setFeature(faceFeature);
            featureItem.setFace(face);
            featureItems.add(featureItem);
        }
        List<Long> insertIds = milvusService.insertFeatures(featureItems);
        if (insertIds.size() < 1){
            faceService.updateFaceStatus(insertIds);
        }
        return new Result().ok();
    }

    private String encodeUrl(String url) throws UnsupportedEncodingException {
        int indexOf = url.indexOf('?');
        String query = url.substring(indexOf + 1);
        String host = url.substring(0,indexOf + 1);
        String equery = URLEncoder.encode(query, "utf-8");
        return host+equery;
    }
}
