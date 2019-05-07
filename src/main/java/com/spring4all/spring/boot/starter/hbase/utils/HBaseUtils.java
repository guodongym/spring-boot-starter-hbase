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

    public static Map<String, byte[]> byteResultHandler(Result result) {
        Map<String, byte[]> map = new HashMap<>(16);
        for (Cell cell : result.rawCells()) {
            map.put(Bytes.toString(CellUtil.cloneQualifier(cell)), CellUtil.cloneValue(cell));
        }
        return map;
    }

    public static Map<String, Object> resultHandler(Result result) {
        Map<String, Object> map = new HashMap<>(16);
        for (Cell cell : result.rawCells()) {
            map.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
        }
        return map;
    }
}
