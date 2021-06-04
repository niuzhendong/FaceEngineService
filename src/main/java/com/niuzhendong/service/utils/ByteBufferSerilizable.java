package com.niuzhendong.service.utils;

import java.util.ArrayList;
import java.util.List;

public class ByteBufferSerilizable {

    /**
     * 解法0；（可能性最大）
     * 将 byte[2056] 转化为 List<float> bit长度不变，分析得维度应为 514，  然后全部升格为 list<Double> 维度不变，bit占用变为 4,112
     * 514 维度 milvus 以float 为数字存储，bit占用 4,112
     * 解法1；（可能性较小，c++版本源码中多为float）
     * 将 byte[2056] 转化为 List<Double> bit长度不变，分析得维度应为 257
     * 257 维度 milvus 以double 为数字存储，bit占用 2056
     * 解法2：（理论可操作性有，但是维度过多）
     * 将 byte[2056] 升格为 List<Double> bit长度变为16,448，维度应为 2056
     * TODO 可分别测试转化后的搜索情况
     */
    public static List<Float> byteUpToFloatListOTL(byte[] bytes){
        List<Float> list = new ArrayList<>();
        for (byte b : bytes) {
            list.add(upToDoubleOTL(b));
        }
        return list;
    }

    // byte 数值升格 float
    public static float upToDoubleOTL(byte b) {
        int bit = b;float i = bit;return i;
    }


    public static byte[] floatDownToByteListOTL(List<Float> floats){
        byte[] list = new byte[2056];
        for (int i=0;i<floats.size();i++) {
            list[i] = downToByteOTL(floats.get(i));
        }
        return list;
    }

    // byte 数值升格 float
    public static byte downToByteOTL(float b) {
        int bit = Math.round(b);
        byte i = (byte) bit;
        return i;
    }
}
