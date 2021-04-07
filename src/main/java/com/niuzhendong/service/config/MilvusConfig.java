package com.niuzhendong.service.config;

import io.milvus.client.ConnectParam;
import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusGrpcClient;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MilvusConfig {

    @Autowired
    private MilvusProp milvusProp;

    /**
     * 获取MinioClient
     */
    @Bean
    public MilvusClient milvusClient() {
        ConnectParam connectParam = new ConnectParam.Builder().withHost(milvusProp.getHost()).withPort(milvusProp.getPort()).build();
        return new MilvusGrpcClient(connectParam);
    }

}
