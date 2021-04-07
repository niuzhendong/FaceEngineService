package com.niuzhendong.service.service.impl;

import com.niuzhendong.service.service.MinioService;
import com.niuzhendong.service.utils.MinIoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    private MinIoUtils minIoUtils;

    @Override
    public void deleteFile(String bucketname,String fileName) {
        minIoUtils.removeObject(bucketname,fileName);
    }

    @Override
    public String uploadFile(InputStream inputStream,String bucketname, String fileName, String contentType) {
        if (!minIoUtils.bucketExists(bucketname)){
            minIoUtils.makeBucket(bucketname);
        }
        minIoUtils.putObject(bucketname,fileName,inputStream);
        return minIoUtils.getObjectUrl(bucketname,fileName);
    }
}
