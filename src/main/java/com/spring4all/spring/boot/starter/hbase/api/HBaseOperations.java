package com.spring4all.spring.boot.starter.hbase.api;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Scan;

import java.util.List;
import java.util.Map;

/**
 * Interface that specifies a basic set of Hbase operations, implemented by {@link HBaseTemplate}. Not often used,
 * but a useful option to enhance testability, as it can easily be mocked or stubbed.
 *
 * @author zhaoguodong
 * @author Costin Leau
 * @author Shaun Elliott
 * @author JThink
 */
public interface HBaseOperations {

    /**
     * Executes the given action against the specified table handling resource management.
     * <p>
     * Application exceptions thrown by the action object get propagated to the caller (can only be unchecked).
     * Allows for returning a result object (typically a domain object or collection of domain objects).
     *
     * @param tableName the target table
     * @param action    action type, implemented by {@link TableCallback}
     * @return the result object of the callback action, or null
     */
    <T> T execute(String tableName, TableCallback<T> action);

    /**
     * Scans the target table, using the given column family.
     * The content is processed row by row by the given action, returning a list of domain objects.
     *
     * @param tableName target table
     * @param family    column family
     * @param mapper    mapper type, implemented by {@link RowMapper}
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(String tableName, String family, final RowMapper<T> mapper);

    /**
     * Scans the target table, using the given column family.
     * The content is processed row by row by the given action, returning a list of domain objects.
     *
     * @param tableName target table
     * @param family    column family
     * @param qualifier column qualifier
     * @param mapper    mapper type, implemented by {@link RowMapper}
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(String tableName, String family, String qualifier, final RowMapper<T> mapper);

    /**
     * 扫描数据返回map
     *
     * @param tableName target table
     * @param scan      table scanner
     * @return a list of the scanned rows
     */
    List<Map<String, byte[]>> find(String tableName, Scan scan);

    /**
     * 使用scan扫描结果，在ScannerCallback中利用ResultScanner处理业务逻辑，不需要关心资源的开闭
     *
     * @param tableName       表名
     * @param scan            扫描配置
     * @param scannerCallback 执行回调
     * @return 返回处理结果
     */
    <T> T find(String tableName, Scan scan, ScannerCallback<T> scannerCallback);

    /**
     * Scans the target table using the given {@link Scan} object. Suitable for maximum control over the scanning
     * process.
     * The content is processed row by row by the given action, returning a list of domain objects.
     *
     * @param tableName target table
     * @param scan      table scanner
     * @param mapper    mapper type, implemented by {@link RowMapper}
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(String tableName, final Scan scan, final RowMapper<T> mapper);


    /**
     * 根据rowKey范围获取总条数，使用协处理器服务端并行统计
     *
     * @param tableName 表名
     * @param startRow  起始row
     * @param stopRow   结束row
     * @return 数据条数
     */
    long findRowCount(String tableName, String startRow, String stopRow);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName target table
     * @param rowName   row name
     * @param mapper    mapper type, implemented by {@link RowMapper}
     * @return object mapping the target row
     */
    <T> T get(String tableName, String rowName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName  target table
     * @param rowName    row name
     * @param familyName column family
     * @param mapper     mapper type, implemented by {@link RowMapper}
     * @return object mapping the target row
     */
    <T> T get(String tableName, String rowName, String familyName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName  target table
     * @param rowName    row name
     * @param familyName family
     * @param qualifier  column qualifier
     * @param mapper     mapper type, implemented by {@link RowMapper}
     * @return object mapping the target row
     */
    <T> T get(String tableName, final String rowName, final String familyName, final String qualifier, final RowMapper<T> mapper);

    /**
     * 执行put update or delete
     *
     * @param tableName target table
     * @param action    action type, implemented by {@link MutatorCallback}
     */
    void execute(String tableName, MutatorCallback action);

    /**
     * 单条新增或者修改
     *
     * @param tableName target table
     * @param mutation  数据
     */
    void saveOrUpdate(String tableName, Mutation mutation);

    /**
     * 批量新增或者修改
     *
     * @param tableName target table
     * @param mutations 数据
     */
    void saveOrUpdates(String tableName, List<Mutation> mutations);
}