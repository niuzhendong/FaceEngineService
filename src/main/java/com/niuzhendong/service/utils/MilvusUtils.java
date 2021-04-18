package com.niuzhendong.service.utils;

import com.google.gson.JsonObject;
import com.niuzhendong.service.config.MilvusProp;
import com.niuzhendong.service.dto.FeatureItem;
import io.milvus.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(MilvusUtils.class);

    /**
     * 插入特征向量
     * @param featureMaps 特征向量集合
     * @return
     */
    public List<Long> insertFeatures(List<FeatureItem> featureMaps){

        /**
         * 解法1；（可能性最大）
         * 将 byte[2056] 转化为 List<float> bit长度不变，分析得维度应为 514，  然后全部升格为 list<Double> 维度不变，bit占用变为 4,112
         * 514 维度 milvus 以double 为数字存储，bit占用 4,112
         * 解法1；（可能性较小，c++版本源码中多为float）
         * 将 byte[2056] 转化为 List<Double> bit长度不变，分析得维度应为 257
         * 257 维度 milvus 以double 为数字存储，bit占用 2056
         * 解法2：（理论可操作性有，但是维度过多）
         * 将 byte[2056] 升格为 List<Double> bit长度变为16,448，维度应为 16,448
         * TODO 可分别测试转化后的搜索情况
         */

        HasCollectionResponse flagRep = milvusClient.hasCollection(milvusProp.getCollectionName());
        if (flagRep.ok()) {
            if (!flagRep.hasCollection()){
                createCollect(257,4096,MetricType.L2);
                createIndex(16384);
            }
        }
        List<ByteBuffer> features = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        //byte[] f = {1.0,0,1.2};
        for (FeatureItem feature : featureMaps){
            ids.add(feature.getFace().getId());
            features.add(ByteBuffer.wrap(feature.getFeature().getFeatureData()));
        }
        //插入特征向量
        InsertParam insertParam = new InsertParam.Builder(milvusProp.getCollectionName()).withVectorIds(ids).withBinaryVectors(features).build();
        InsertResponse insertResponse = milvusClient.insert(insertParam);
        milvusClient.flush(milvusProp.getCollectionName());
        boolean flag = insertResponse.ok();
        if(!flag){
            logger.info("插入失败!!!服务端信息："+insertResponse.getResponse().getMessage());
            return new ArrayList<>();
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

        HasCollectionResponse flagRep = milvusClient.hasCollection(milvusProp.getCollectionName());
        if (flagRep.ok()) {
            if (!flagRep.hasCollection()){
                return null;
            }
        }
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
        final IndexType indexType = IndexType.FLAT;
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
