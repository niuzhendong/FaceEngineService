package com.niuzhendong.service.service.impl;

import com.niuzhendong.service.dto.FeatureItem;
import com.niuzhendong.service.service.MilvusService;
import com.niuzhendong.service.utils.MilvusUtils;
import io.milvus.client.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.List;

@Service
public class MilvusServiceImpl implements MilvusService {

    @Autowired
    MilvusUtils milvusUtils;

    @Override
    public List<Long> insertFeatures(List<FeatureItem> features) {
        return milvusUtils.insertFeatures(features);
    }

    @Override
    public SearchResponse searchFeature(List<ByteBuffer> vectorsToSearch, long topK) {
        return milvusUtils.searchFeature(vectorsToSearch,topK);
    }

    @Override
    public boolean createIndex(int nlist) {
        return milvusUtils.createIndex(nlist);
    }
}
