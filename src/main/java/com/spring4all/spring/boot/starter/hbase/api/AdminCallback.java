package com.spring4all.spring.boot.starter.hbase.api;

import org.apache.hadoop.hbase.client.Admin;

/**
 * Callback interface for Hbase code. To be used with {@link HBaseTemplate}'s execution methods, often as anonymous classes within a method implementation without
 * having to worry about exception handling.
 *
 * @author Costin Leau
 * @author JThink
 */
public interface AdminCallback<T> {

    /**
     * 由{@link HBaseTemplate}调用对HBase进行维护管理
     *
     * @param admin hbase管理客户端
     * @return a result object, or null if none
     * @throws Throwable thrown by the Hbase API
     */
    T doInAdmin(Admin admin) throws Throwable;
}