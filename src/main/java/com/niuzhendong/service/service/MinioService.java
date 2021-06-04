package com.niuzhendong.service.service;

import java.io.IOException;
import java.io.InputStream;

public interface MinioService {
    void deleteFile(String bucketName,String fileName);
    String uploadFile(InputStream inputStream,String bucketName, String fileName);
    InputStream getFileFromMinio(String bucketName,String fileName);
    String getUrlFromMinio(String bucketName,String fileName);
    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @Retrun
     */
    InputStream getFileFromUrl(String urlStr);
}
