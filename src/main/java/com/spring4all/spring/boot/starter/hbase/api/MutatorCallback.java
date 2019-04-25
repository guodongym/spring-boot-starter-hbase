package com.spring4all.spring.boot.starter.hbase.api;

import org.apache.hadoop.hbase.client.BufferedMutator;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * desc： callback for hbase put delete and update
 * date： 2016-12-08 14:31:34
 */
public interface MutatorCallback {

    /**
     * 使用mutator api to update put and delete
     *
     * @param mutator 更新或者删除的数据
     * @throws Throwable 异常抛出
     */
    void doInMutator(BufferedMutator mutator) throws Throwable;
}
