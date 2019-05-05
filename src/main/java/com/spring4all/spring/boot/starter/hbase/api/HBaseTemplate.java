package com.spring4all.spring.boot.starter.hbase.api;

import com.spring4all.spring.boot.starter.hbase.page.Column;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Central class for accessing the HBase API. Simplifies the use of HBase and helps to avoid common errors.
 * It executes core HBase workflow, leaving application code to invoke actions and extract results.
 *
 * @author zhaoguodong
 * @author Costin Leau
 * @author Shaun Elliott
 * @author JThink
 */
public class HBaseTemplate implements HBaseOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseTemplate.class);

    private Configuration configuration;

    private volatile Connection connection;

    public HBaseTemplate(Configuration configuration) {
        this.setConfiguration(configuration);
        Assert.notNull(configuration, " a valid configuration is required");
    }

    @Override
    public <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        StopWatch sw = new StopWatch("HBase Query");
        sw.start();
        Table table = null;
        try {
            table = this.getConnection().getTable(TableName.valueOf(tableName));
            return action.doInTable(table);
        } catch (Throwable throwable) {
            throw new HBaseSystemException(throwable);
        } finally {
            if (null != table) {
                try {
                    table.close();
                    sw.stop();
                    LOGGER.info(sw.shortSummary());
                } catch (IOException e) {
                    LOGGER.error("hbase资源释放失败", e);
                }
            }
        }
    }

    @Override
    public <T> List<T> find(String tableName, String family, final RowMapper<T> mapper) {
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(family));
        return this.find(tableName, scan, mapper);
    }

    @Override
    public <T> List<T> find(String tableName, String family, String qualifier, final RowMapper<T> mapper) {
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return this.find(tableName, scan, mapper);
    }

    @Override
    public List<Map<String, byte[]>> find(String tableName, final Scan scan) {
        return this.find(tableName, scan, RowMapper.DEFAULT);
    }

    @Override
    public <T> T find(String tableName, final Scan scan, final ScannerCallback<T> scannerCallback) {
        return this.execute(tableName, table -> {
            try (ResultScanner scanner = table.getScanner(scan)) {
                return scannerCallback.doInScanner(scanner);
            }
        });
    }


    @Override
    public <T> List<T> findFirstPage(String tableName, String startRow, String stopRow, int pageSize, RowMapper<T> mapper) {
        return this.findFirstPage(tableName, startRow, stopRow, pageSize, mapper, null, null);
    }

    @Override
    public <T> List<T> findFirstPage(String tableName, String startRow, String stopRow, int pageSize, RowMapper<T> mapper, List<Column> columns, FilterList filterList) {
        final Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow + "_"));
        return this.findFirstOrLastPage(tableName, pageSize, mapper, scan, columns, filterList);
    }

    @Override
    public <T> List<T> findLastPage(String tableName, String startRow, String stopRow, int pageSize, RowMapper<T> mapper) {
        return this.findLastPage(tableName, startRow, stopRow, pageSize, mapper, null, null);
    }

    @Override
    public <T> List<T> findLastPage(String tableName, String startRow, String stopRow, int pageSize, RowMapper<T> mapper, List<Column> columns, FilterList filterList) {
        final Scan scan = new Scan();
        scan.setReversed(true);
        scan.setStartRow(Bytes.toBytes(stopRow));
        scan.setStopRow(Bytes.toBytes(startRow + "_"));
        return this.findFirstOrLastPage(tableName, pageSize, mapper, scan, columns, filterList);
    }

    @Override
    public <T> List<T> findPreviousPage(String tableName, String startRow, String stopRow, int pageSize, RowMapper<T> mapper) {
        return this.findPreviousPage(tableName, startRow, stopRow, pageSize, mapper, null, null);
    }

    @Override
    public <T> List<T> findPreviousPage(String tableName, String startRow, String stopRow, int pageSize, RowMapper<T> mapper, List<Column> columns, FilterList filterList) {
        final Scan scan = new Scan();
        scan.setReversed(true);
        scan.setStartRow(Bytes.toBytes(stopRow));
        scan.setStopRow(Bytes.toBytes(startRow + "_"));

        return this.findPage(tableName, pageSize, mapper, scan, columns, filterList);
    }

    @Override
    public <T> List<T> findNextPage(String tableName, String startRow, String stopRow, int pageSize, RowMapper<T> mapper) {
        return this.findNextPage(tableName, startRow, stopRow, pageSize, mapper, null, null);
    }

    @Override
    public <T> List<T> findNextPage(String tableName, String startRow, String stopRow, int pageSize, RowMapper<T> mapper, List<Column> columns, FilterList filterList) {
        final Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow + "_"));

        return this.findPage(tableName, pageSize, mapper, scan, columns, filterList);
    }

    @Override
    public <T> List<T> findPage(String tableName, String startRow, String stopRow, int pageSize,
                                String pageFirstRowKey, String pageLastRowKey, boolean isAsc, Boolean isNext,
                                RowMapper<T> mapper, List<Column> columns, FilterList filterList) {

        Assert.notNull(pageFirstRowKey, "pageFirstRowKey must not be null");
        Assert.notNull(pageLastRowKey, "pageLastRowKey must not be null");

        List<T> page;
        if (isNext == null) {
            if (isAsc) {
                page = this.findFirstPage(tableName, startRow, stopRow, pageSize, mapper, columns, filterList);
            } else {
                page = this.findLastPage(tableName, startRow, stopRow, pageSize, mapper, columns, filterList);
            }
        } else if (isNext) {
            if (isAsc) {
                page = this.findNextPage(tableName, pageLastRowKey, stopRow, pageSize, mapper, columns, filterList);
            } else {
                page = this.findPreviousPage(tableName, startRow, pageFirstRowKey, pageSize, mapper, columns, filterList);
            }
        } else {
            if (isAsc) {
                page = this.findPreviousPage(tableName, startRow, pageFirstRowKey, pageSize, mapper, columns, filterList);
            } else {
                page = this.findNextPage(tableName, pageLastRowKey, stopRow, pageSize, mapper, columns, filterList);
            }
        }

        return page;
    }

    private <T> List<T> findFirstOrLastPage(String tableName, int pageSize, RowMapper<T> mapper, Scan scan, List<Column> columns, FilterList filterList) {
        scan.setMaxVersions();

        if (columns != null) {
            for (Column column : columns) {
                scan.addColumn(Bytes.toBytes(column.getFamily()), Bytes.toBytes(column.getQualifier()));
            }
        }

        filterList = setPageFilter(filterList, pageSize);
        scan.setFilter(filterList);

        List<T> result = this.find(tableName, scan, mapper);
        if (result.size() < pageSize) {
            return result;
        }
        return result.subList(0, pageSize);
    }

    private <T> List<T> findPage(String tableName, int pageSize, RowMapper<T> mapper, Scan scan, List<Column> columns, FilterList filterList) {
        scan.setMaxVersions();

        if (columns != null) {
            for (Column column : columns) {
                scan.addColumn(Bytes.toBytes(column.getFamily()), Bytes.toBytes(column.getQualifier()));
            }
        }

        final int getSize = pageSize + 1;
        filterList = setPageFilter(filterList, getSize);
        scan.setFilter(filterList);

        List<T> result = this.find(tableName, scan, mapper);
        if (result.size() < getSize) {
            return result.subList(1, result.size());
        }
        return result.subList(1, getSize);
    }

    private FilterList setPageFilter(FilterList filterList, int pageSize) {
        if (filterList == null) {
            filterList = new FilterList();
        }
        final PageFilter pageFilter = new PageFilter(pageSize);
        filterList.addFilter(pageFilter);
        return filterList;
    }


    @Override
    public <T> List<T> findPage(String tableName, String startRow, String stopRow,
                                int pageNo, int pageSize, boolean isAsc,
                                RowMapper<T> mapper, List<Column> columns, FilterList filterList) {
        if (pageSize == 0) {
            pageSize = 10;
        }
        if (pageNo == 0) {
            pageNo = 1;
        }
        // 计算起始页和结束页
        int pageStartNo = (pageNo - 1) * pageSize;
        int pageEndNo = pageStartNo + pageSize;

        final Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow + "_"));
        scan.setMaxVersions();

        if (!isAsc) {
            scan.setReversed(true);
        }

        if (columns != null) {
            for (Column column : columns) {
                scan.addColumn(Bytes.toBytes(column.getFamily()), Bytes.toBytes(column.getQualifier()));
            }
        }

        if (filterList == null) {
            filterList = new FilterList();
        }
        filterList.addFilter(new FirstKeyOnlyFilter());
        scan.setFilter(filterList);

        final int finalPageSize = pageSize;
        final List<String> rowList = this.execute(tableName, table -> {
            try (ResultScanner scanner = table.getScanner(scan)) {
                List<String> rs = new ArrayList<>();
                int rowNum = 0;
                for (Result result : scanner) {
                    if (finalPageSize == rs.size()) {
                        break;
                    }
                    if (rowNum >= pageStartNo && rowNum < pageEndNo) {
                        rs.add(Bytes.toString(result.getRow()));
                    }
                    rowNum++;
                }
                return rs;
            }
        });

        return this.multiGet(tableName, mapper, rowList.toArray(new String[]{}));
    }

    @Override
    public <T> List<T> find(String tableName, final Scan scan, final RowMapper<T> mapper) {
        return this.execute(tableName, table -> {
            try (ResultScanner scanner = table.getScanner(scan)) {
                List<T> rs = new ArrayList<>();
                int rowNum = 0;
                for (Result result : scanner) {
                    rs.add(mapper.mapRow(result, rowNum++));
                }
                return rs;
            }
        });
    }

    @Override
    public long findRowCount(String tableName, String startRow, String stopRow) {
        final AggregationClient aggregationClient = new AggregationClient(this.configuration);
        final Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow));
        return this.execute(tableName, table -> {
            return aggregationClient.rowCount(table, new LongColumnInterpreter(), scan);
        });
    }

    @Override
    public <T> T get(String tableName, String rowName, final RowMapper<T> mapper) {
        return this.get(tableName, rowName, null, null, mapper);
    }

    @Override
    public <T> T get(String tableName, String rowName, String familyName, final RowMapper<T> mapper) {
        return this.get(tableName, rowName, familyName, null, mapper);
    }

    @Override
    public <T> T get(String tableName, final String rowName, final String familyName, final String qualifier, final RowMapper<T> mapper) {
        return this.execute(tableName, table -> {
            Get get = new Get(Bytes.toBytes(rowName));
            get.setMaxVersions();
            if (StringUtils.isNotBlank(familyName)) {
                byte[] family = Bytes.toBytes(familyName);
                if (StringUtils.isNotBlank(qualifier)) {
                    get.addColumn(family, Bytes.toBytes(qualifier));
                } else {
                    get.addFamily(family);
                }
            }
            Result result = table.get(get);
            return mapper.mapRow(result, 0);
        });
    }

    @Override
    public <T> List<T> multiGet(String tableName, final RowMapper<T> mapper, final String... rowNames) {
        return this.execute(tableName, table -> {
            List<Get> gets = new ArrayList<>();
            for (String row : rowNames) {
                final Get get = new Get(Bytes.toBytes(row));
                get.setMaxVersions();
                gets.add(get);
            }
            Result[] results = table.get(gets);

            List<T> rs = new ArrayList<>();
            int rowNum = 0;
            for (Result result : results) {
                rs.add(mapper.mapRow(result, rowNum++));
            }
            return rs;
        });
    }

    @Override
    public void execute(String tableName, MutatorCallback action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        StopWatch sw = new StopWatch("HBase SaveOrUpdate");
        sw.start();
        BufferedMutator mutator = null;
        try {
            BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));
            mutator = this.getConnection().getBufferedMutator(mutatorParams.writeBufferSize(3 * 1024 * 1024));
            action.doInMutator(mutator);
        } catch (Throwable throwable) {
            sw.stop();
            throw new HBaseSystemException(throwable);
        } finally {
            if (null != mutator) {
                try {
                    mutator.flush();
                    mutator.close();
                    sw.stop();
                    LOGGER.info(sw.shortSummary());
                } catch (IOException e) {
                    LOGGER.error("hbase mutator资源释放失败", e);
                }
            }
        }
    }

    @Override
    public void saveOrUpdate(String tableName, final Mutation mutation) {
        this.execute(tableName, mutator -> {
            mutator.mutate(mutation);
        });
    }

    @Override
    public void saveOrUpdates(String tableName, final List<Mutation> mutations) {
        this.execute(tableName, mutator -> {
            mutator.mutate(mutations);
        });
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        if (null == this.connection) {
            synchronized (this) {
                if (null == this.connection) {
                    try {
                        this.connection = ConnectionFactory.createConnection(configuration);
                    } catch (IOException e) {
                        LOGGER.error("hbase connection资源池创建失败");
                    }
                }
            }
        }
        return this.connection;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
