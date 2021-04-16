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
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://81.69.46.151:9000/face/632127199212200019%E7%89%9B%E6%8C%AF%E4%B8%9C.jpg?Content-Disposition=attachment%3B%20filename%3D%22632127199212200019%E7%89%9B%E6%8C%AF%E4%B8%9C.jpg%22&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minio%2F20210416%2F%2Fs3%2Faws4_request&X-Amz-Date=20210416T021558Z&X-Amz-Expires=432000&X-Amz-SignedHeaders=host&X-Amz-Signature=2f978fb31ed618fdfc7f944f07d5f26b0a3c15eeee7e47952a20d06ff90977fe").build();
            Response response = client.newCall(request).execute();
            inputStream = response.body().byteStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
