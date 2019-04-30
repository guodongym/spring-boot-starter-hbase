package com.spring4all.spring.boot.starter.hbase.api;

import org.apache.hadoop.hbase.client.ResultScanner;

/**
 * ResultScanner的回调
 *
 * @param <T>
 * @author zhaogd
 */
public interface ScannerCallback<T> {

    /**
     * 由{@link HBaseTemplate}执行，并使用ResultScanner进行调用,不需要关心scanner的关闭
     *
     * @param scanner hbase ResultScanner
     * @return a result object, or null if none
     * @throws Throwable thrown by the Hbase API
     */
    T doInScanner(ResultScanner scanner) throws Throwable;
}