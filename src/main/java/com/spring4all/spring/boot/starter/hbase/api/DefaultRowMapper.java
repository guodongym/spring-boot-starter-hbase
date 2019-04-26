package com.spring4all.spring.boot.starter.hbase.api;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link RowMapper}的默认实现返回{@link Map}
 *
 * @author zhaogd
 * @date 2019/4/26
 */
public class DefaultRowMapper implements RowMapper<Map<String, byte[]>> {

    @Override
    public Map<String, byte[]> mapRow(Result result, int rowNum) {

        Map<String, byte[]> map = new HashMap<>(10);
        for (Cell cell : result.rawCells()) {
            map.put(new String(CellUtil.cloneQualifier(cell)), CellUtil.cloneValue(cell));
        }
        return map;
    }
}
