package com.niuzhendong.service.utils;

import io.milvus.client.MilvusClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MilvusUtils {

    @Autowired
    MilvusClient milvusClient;


}
