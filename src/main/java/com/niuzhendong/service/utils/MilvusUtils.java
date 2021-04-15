package com.niuzhendong.service.utils;

import com.google.gson.JsonObject;
import com.niuzhendong.service.config.MilvusProp;
import com.niuzhendong.service.dto.FeatureItem;
import io.milvus.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Component
public class MilvusUtils {

    @Autowired
    MilvusClient milvusClient;

    @Autowired
    MilvusProp milvusProp;

    /**
     * 插入特征向量
     * @param featureMaps 特征向量集合
     * @return
     */
    public List<Long> insertFeatures(List<FeatureItem> featureMaps){

        HasCollectionResponse flagRep = milvusClient.hasCollection(milvusProp.getCollectionName());
        if (!flagRep.ok()) {
            createCollect(2056,4096,MetricType.L2);
            createIndex(16384);
        }

        List<ByteBuffer> features = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        for (FeatureItem feature : featureMaps){
            //ids.add(feature.getId());
            ids.add(111111l);
            features.add(ByteBuffer.wrap(feature.getFeature().getFeatureData()));
        }

        //插入特征向量
        InsertParam insertParam = new InsertParam.Builder(milvusProp.getCollectionName()).withVectorIds(ids).withBinaryVectors(features).build();
        InsertResponse insertResponse = milvusClient.insert(insertParam);
        milvusClient.flush(milvusProp.getCollectionName());

        boolean flag = insertResponse.ok();
        if(!flag){
            System.out.println("插入失败");
            return null;
        }

        List<Long> vectorIds = insertResponse.getVectorIds();
        return vectorIds;
    }


    /**
     * 查询相似的特诊向量
     * @param vectorsToSearch
     * @param topK
     * @return
     */
    public SearchResponse searchFeature(List<ByteBuffer> vectorsToSearch, long topK, int nprobe){

        JsonObject indexParamsJson = new JsonObject();
        indexParamsJson.addProperty("nprobe", nprobe);   //nprobe代表选择最近的多少个聚类去比较。
        SearchParam searchParam =new SearchParam.Builder(milvusProp.getCollectionName()).withBinaryVectors(vectorsToSearch)
                .withParamsInJson(indexParamsJson.toString())
                .withTopK(topK)
                .build();
        SearchResponse searchResponse = milvusClient.search(searchParam);
        return searchResponse;
    }

    public SearchResponse searchFeature(List<ByteBuffer> vectorsToSearch, long topK){
        return searchFeature(vectorsToSearch, topK, 15);
    }

    /**
     * 创建数据库表
     * @param dimension 向量维度
     * @param indexFileSize 单个文件的大小值
     * @param metricType 距离算法类型
     * @return
     */
    private Response createCollect(int dimension, int indexFileSize,MetricType metricType){
        CollectionMapping collectionMapping = new CollectionMapping.Builder(milvusProp.getCollectionName(), dimension)
                .withIndexFileSize(indexFileSize)
                .withMetricType(metricType)
                .build();
        Response response = milvusClient.createCollection(collectionMapping);
        return response;
    }

    /**
     * 创建数据索引
     * @param nlist 聚类数
     * @return
     */
    public boolean createIndex(int nlist){
        final IndexType indexType = IndexType.IVF_SQ8_H;
        JsonObject indexParamsJson = new JsonObject();
        indexParamsJson.addProperty("nlist", nlist);   //nlist代表聚类数，根据数据量多少设置
        Index index =
                new Index.Builder(milvusProp.getCollectionName(), indexType)
                        .withParamsInJson(indexParamsJson.toString())
                        .build();
        Response createIndexResponse = milvusClient.createIndex(index);
        return createIndexResponse.ok();
    }
}
