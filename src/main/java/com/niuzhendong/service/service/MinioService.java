package com.niuzhendong.service.service;

import java.io.InputStream;

public interface MinioService {
    public void deleteFile(String bucketname,String fileName);
    public String uploadFile(InputStream inputStream,String bucketname, String fileName, String contentType);
}
