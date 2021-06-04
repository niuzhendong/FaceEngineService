package com.niuzhendong.service.controller;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class FaceServiceController {

    @Autowired
    private FaceService faceService;

    @Autowired
    private MilvusService milvusService;

    @Autowired
    private MinioService minioService;

    @RequestMapping(value = "/api/compareFaceFeature", method = RequestMethod.GET)
    public Result<List<CompareInfo>> compareFaceFeature(@RequestParam String url) throws UnsupportedEncodingException{

        List<CompareInfo> compareInfos = new ArrayList<>();
        InputStream inputStream = minioService.getFileFromUrl(encodeUrl(url));
        if (inputStream == null){
            return new Result<List<CompareInfo>>().error(500,"图像获取异常，请检查图像链接");
        }
        ImageInfo imageInfo = ImageFactory.getRGBData(inputStream);
        List<FaceInfo> faceInfos = faceService.faceExists(imageInfo);
        if (faceInfos.size() < 1){
            return new Result<List<CompareInfo>>().error(500,"图片中未检测出人脸，请检查图像");
        }
        if(faceInfos.size() > 1){
            return new Result<List<CompareInfo>>().error(500,"图片中包含多个人脸，请检查图像");
        }
        //遮挡值为0,表示没有戴口罩的场景
        FeatureItem featureItem = faceService.imageQualityDetectForSingle(imageInfo,0,faceInfos.get(0));
        //根据文档阈值设置建议，仅对图像质量大于0.49的图像进行识别（没有口罩的场景）
        if (featureItem.getImageQuality().getFaceQuality() > 0.49) {
            FaceFeature faceFeature = faceService.getImageFeature(imageInfo,featureItem.getFaceInfo());
            //检测结果封装到内部流转数据结构中
            featureItem.setFeature(faceFeature);
            //构造人脸体征查询队列、为保证查询关联性，单一人脸传入，多次查询
            //topK,返回匹配项
            SearchResponse searchResponse = milvusService.searchFeature(faceFeature.getFeatureData(),10);
            if (searchResponse.getResponse().ok()){
                List<Long> ids = searchResponse.getResultIdsList().get(0);
                if (ids.size() > 0){
                    List<Face> faces = faceService.findFaceInfo(ids);
                    for (int i = 0;i<faces.size();i++){
                        Face face = faces.get(i);
                        CompareInfo compareInfo = new CompareInfo();
                        compareInfo.setId(face.getPeoId());
                        compareInfo.setTag(1);
                        compareInfo.setUrl(minioService.getUrlFromMinio(face.getBucketName(),face.getFileName()));
                        float dis = Float.valueOf(searchResponse.getResultDistancesList().get(0).get(i));
                        compareInfo.setDistance((134742016-dis)/134742016);
                        compareInfos.add(compareInfo);
                    }
                }else {
                    return new Result<List<CompareInfo>>().error(500,"未发现匹配项");
                }
            }
        } else {
            return new Result<List<CompareInfo>>().error(500,"图片质量低于检测最低值，请检查图像");
        }
        return new Result<List<CompareInfo>>().ok(compareInfos);
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
            return new Result<CompareInfo>().error(500,"图片中包含多个人脸，请检查图像");
        }
        //遮挡值为0,表示没有戴口罩的场景
        FeatureItem featureItem = faceService.imageQualityDetectForSingle(imageInfo,0,faceInfos.get(0));
        //根据文档阈值设置建议，仅对图像质量大于0.49的图像进行识别（没有口罩的场景）
        if (featureItem.getImageQuality().getFaceQuality() > 0.49) {
            FaceFeature faceFeature = faceService.getImageFeature(imageInfo,featureItem.getFaceInfo());
            //检测结果封装到内部流转数据结构中
            featureItem.setFeature(faceFeature);
            //构造人脸体征查询队列、为保证查询关联性，单一人脸传入，多次查询
            //topK,返回1个匹配项
            SearchResponse searchResponse = milvusService.searchFeature(faceFeature.getFeatureData(),1);
            if (searchResponse.getResponse().ok()){
                List<Long> ids = searchResponse.getResultIdsList().get(0);
                if (ids.size() > 0){
                    List<Face> faces = faceService.findFaceInfo(ids);
                    FaceFeature taff = new FaceFeature();
                    taff.setFeatureData(faces.get(0).getFeature());
                    FaceSimilar similar = faceService.compareFaceFeature(taff,faceFeature);
                    if (similar.getScore() >= 0.82){
                        compareInfo.setId(faces.get(0).getPeoId());
                        compareInfo.setTag(1);
                        compareInfo.setUrl(minioService.getUrlFromMinio(faces.get(0).getBucketName(),faces.get(0).getFileName()));
                        compareInfo.setDistance(similar.getScore());
                    }else {
                        compareInfo.setId(String.valueOf(ids.get(0)));
                        compareInfo.setTag(0);
                        minioService.uploadFile(new ByteArrayInputStream(featureItem.getFaceInfo().getFaceData()),"illegal",ids.get(0)+".jpg");
                        compareInfo.setUrl(minioService.getUrlFromMinio("illegal",ids.get(0)+".jpg"));
                    }
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
        List<Face> faces = faceService.getFaceInfo();
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
            face.setFeature(faceFeature.getFeatureData());
            faceService.updateFace(face);
        }
        List<Long> insertIds = milvusService.insertFeatures(featureItems);
        if (insertIds.size() < 1){
            faceService.updateFaceStatus(insertIds);
        }
        return new Result().ok();
    }

    private String encodeUrl(String url) throws UnsupportedEncodingException {
        int indexOf = url.indexOf('?');
        if(indexOf != -1){
            String query = url.substring(indexOf + 1);
            String host = url.substring(0,indexOf + 1);
            String[] pm = query.split(String.valueOf('&'));
            if (pm.length!=0){
                String[] pm0 = pm[0].split(String.valueOf('='));
                //此段代码仅针对minio中特殊字符传参的问题
                if (pm0.length == 3 && pm0[0].equals("Content-Disposition")){
                    pm[0] = pm0[0]+"="+URLEncoder.encode(pm0[1], "utf-8")+"%3D"+URLEncoder.encode(pm0[2], "utf-8");
                }
                query = Arrays.stream(pm).collect(Collectors.joining("&"));
            }
            return host+query;
        }else {
            return url;
        }
    }
}
