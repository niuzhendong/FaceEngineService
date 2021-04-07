package com.niuzhendong.service.utils;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class MinIoUtils {

    @Autowired
    private MinioClient client;

    /**
     * 判断 bucket是否存在
     * @param bucketName
     * @return
     */
    public boolean bucketExists(String bucketName){
        try {
            return client.bucketExists(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建 bucket
     * @param bucketName
     */
    public void makeBucket(String bucketName){
        try {
            boolean isExist = client.bucketExists(bucketName);
            if(!isExist) {
                client.makeBucket(bucketName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param bucketName
     * @param objectName
     * @param filename
     */
    public void putObject(String bucketName, String objectName, String filename){
        try {
            client.putObject(bucketName,objectName,filename,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 文件上传
     * @param bucketName
     * @param objectName
     * @param stream
     */
    public void putObject(String bucketName, String objectName, InputStream stream){
        try {
            client.putObject(bucketName,objectName,stream,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除文件
     * @param bucketName
     * @param objectName
     */
    public void removeObject(String bucketName, String objectName){
        try {
            client.removeObject(bucketName,objectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取文件url
     * @param bucketName
     * @param objectName
     */
    public String getObjectUrl(String bucketName, String objectName){
        try {
            return client.presignedGetObject(bucketName,objectName,60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
