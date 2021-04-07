package com.niuzhendong.service;

import com.niuzhendong.service.service.MinioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest
public class MinioServiceTest {

    @Autowired
    private MinioService _minioService;

    @Test
    public void uploadTest() {

        try {
            InputStream inputStream = new FileInputStream("C:\\Users\\niuzhendong\\Pictures\\DOAX-VenusVacation\\DOAX-VenusVacation_201115_190337.jpg");
            String url = _minioService.uploadFile(inputStream,"face", "yy.jpg", "image/jpeg");
            System.out.println(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteTest() {

        try {
            _minioService.deleteFile("face","yy.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
