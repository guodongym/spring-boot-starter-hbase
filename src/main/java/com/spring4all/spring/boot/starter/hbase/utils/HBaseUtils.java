package com.spring4all.spring.boot.starter.hbase.utils;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.HashMap;
import java.util.Map;

/**
 * HBase工具类，处理结果转换
 *
 * @author zhaogd
 * @date 2019/5/6
 */
public class HBaseUtils {

    /**
     * HBase结果对象转换为Map<String, byte[]>
     *
     * @param result HBase结果对象
     * @return 转换后的map
     */
    public static Map<String, byte[]> byteResultHandler(Result result) {
        Map<String, byte[]> map = new HashMap<>(16);
        for (Cell cell : result.rawCells()) {
            map.put(Bytes.toString(CellUtil.cloneQualifier(cell)), CellUtil.cloneValue(cell));
        }
        return map;
    }

    /**
     * HBase结果对象转换为Map<String, Object>，注意Object存储的还是String,只是为了满足特殊场景使用
     *
     * @param result HBase结果对象
     * @return 转换后的map
     */
    public static Map<String, Object> objectResultHandler(Result result) {
        Map<String, Object> map = new HashMap<>(16);
        for (Cell cell : result.rawCells()) {
            map.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
        }
        return map;
    }

    /**
     * HBase结果对象转换为Map<String, String>
     *
     * @param result HBase结果对象
     * @return 转换后的map
     */
    public static Map<String, String> resultHandler(Result result) {
        Map<String, String> map = new HashMap<>(16);
        for (Cell cell : result.rawCells()) {
            map.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
        }
        return map;
    }
}
