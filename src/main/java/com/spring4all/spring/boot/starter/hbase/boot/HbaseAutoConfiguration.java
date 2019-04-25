package com.spring4all.spring.boot.starter.hbase.boot;

import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * desc： hbase auto configuration
 * date： 2016-11-16 11:11:27
 */
@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(HbaseProperties.class)
@ConditionalOnClass(HbaseTemplate.class)
public class HbaseAutoConfiguration {

    private final HbaseProperties hbaseProperties;

    @Autowired
    public HbaseAutoConfiguration(HbaseProperties hbaseProperties) {
        this.hbaseProperties = hbaseProperties;
    }

    @Bean
    @ConditionalOnMissingBean(HbaseTemplate.class)
    public HbaseTemplate hbaseTemplate() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", this.hbaseProperties.getQuorum());
        configuration.set("hbase.rootdir", hbaseProperties.getRootDir());
        configuration.set("zookeeper.znode.parent", hbaseProperties.getNodeParent());
        configuration.set("hbase.client.scanner.caching", hbaseProperties.getScannerCaching());
        configuration.set("hbase.client.scanner.timeout.period", hbaseProperties.getScannerTimeoutPeriod());
        configuration.set("hbase.client.retries.number", hbaseProperties.getRetriesNumber());
        configuration.set("hbase.rpc.timeout", hbaseProperties.getRpcTimeout());
        configuration.set("hbase.hconnection.threads.core", hbaseProperties.getConnectionThreadsCore());

        // 设置其他自定义配置
        hbaseProperties.getProperties().forEach(configuration::set);
        return new HbaseTemplate(configuration);
    }
}
