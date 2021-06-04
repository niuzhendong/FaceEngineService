package com.niuzhendong.service.service;

import com.niuzhendong.service.dto.FeatureItem;
import io.milvus.client.SearchResponse;

import java.nio.ByteBuffer;
import java.util.List;

public interface MilvusService {
    List<Long> insertFeatures(List<FeatureItem> features);
    SearchResponse searchFeature(byte[] vectorsToSearch, long topK);
    boolean createIndex(int nlist);
}
