package com.niuzhendong.service.service.impl;

import com.niuzhendong.service.service.MinioService;
import com.niuzhendong.service.utils.MinIoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    @Override
    public InputStream getFileFromMinio(String bucketName, String fileName) {
        return null;
    }

    @Override
    public InputStream getFileFromUrl(String urlStr) {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            inputStream = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return inputStream;
    }
}
