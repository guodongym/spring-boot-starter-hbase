package com.spring4all.spring.boot.starter.hbase.api;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Central class for accessing the HBase API. Simplifies the use of HBase and helps to avoid common errors.
 * It executes core HBase workflow, leaving application code to invoke actions and extract results.
 *
 * @author zhaoguodong
 * @author Costin Leau
 * @author Shaun Elliott
 * @author JThink
 */
public class HbaseTemplate implements HbaseOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseTemplate.class);

    private Configuration configuration;

    private volatile Connection connection;

    public HbaseTemplate(Configuration configuration) {
        this.setConfiguration(configuration);
        Assert.notNull(configuration, " a valid configuration is required");
    }

    @Override
    public <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        StopWatch sw = new StopWatch();
        sw.start();
        Table table = null;
        try {
            table = this.getConnection().getTable(TableName.valueOf(tableName));
            return action.doInTable(table);
        } catch (Throwable throwable) {
            throw new HbaseSystemException(throwable);
        } finally {
            if (null != table) {
                try {
                    table.close();
                    sw.stop();
                } catch (IOException e) {
                    LOGGER.error("hbase资源释放失败");
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
    public void execute(String tableName, MutatorCallback action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        StopWatch sw = new StopWatch();
        sw.start();
        BufferedMutator mutator = null;
        try {
            BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));
            mutator = this.getConnection().getBufferedMutator(mutatorParams.writeBufferSize(3 * 1024 * 1024));
            action.doInMutator(mutator);
        } catch (Throwable throwable) {
            sw.stop();
            throw new HbaseSystemException(throwable);
        } finally {
            if (null != mutator) {
                try {
                    mutator.flush();
                    mutator.close();
                    sw.stop();
                } catch (IOException e) {
                    LOGGER.error("hbase mutator资源释放失败");
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
