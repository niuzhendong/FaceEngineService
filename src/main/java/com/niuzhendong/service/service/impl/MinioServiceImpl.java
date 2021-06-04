package com.niuzhendong.service.service.impl;

import com.niuzhendong.service.service.MinioService;
import com.niuzhendong.service.utils.MinIoUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    public String uploadFile(InputStream inputStream,String bucketname, String fileName) {
        if (!minIoUtils.bucketExists(bucketname)){
            minIoUtils.makeBucket(bucketname);
        }
        minIoUtils.putObject(bucketname,fileName,inputStream);
        return minIoUtils.getObjectUrl(bucketname,fileName);
    }

    @Override
    public InputStream getFileFromMinio(String bucketName, String fileName) {
        return minIoUtils.getObject(bucketName,fileName);
    }

    @Override
    public String getUrlFromMinio(String bucketName, String fileName) {
        return minIoUtils.getObjectUrl(bucketName,fileName);
    }

    @Override
    public InputStream getFileFromUrl(String urlStr) {
        InputStream inputStream = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(urlStr).build();
            Response response = client.newCall(request).execute();
            inputStream = response.body().byteStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
