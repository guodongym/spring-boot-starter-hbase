package com.spring4all.spring.boot.starter.hbase.api;

import org.apache.hadoop.hbase.client.Result;

import java.util.Map;

/**
 * Callback for mapping rows of a {@link org.apache.hadoop.hbase.client.ResultScanner} on a per-row basis.
 * Implementations of this interface perform the actual work of mapping each row to a result object, but don't need to worry about exception handling.
 *
 * @author Costin Leau
 * @author JThink
 */
public interface RowMapper<T> {

    RowMapper<Map<String, byte[]>> DEFAULT = new DefaultRowMapper();

    /**
     * 对{@link Result}进行逐行映射的回调，可以把每一行数据映射为定义的实体
     *
     * @param result hbase查询结果
     * @param rowNum 行号
     * @return 映射之后的实体
     * @throws Exception 异常信息
     */
    T mapRow(Result result, int rowNum) throws Exception;
}
