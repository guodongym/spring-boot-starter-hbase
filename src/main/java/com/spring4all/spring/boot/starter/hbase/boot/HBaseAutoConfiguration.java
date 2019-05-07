package com.spring4all.spring.boot.starter.hbase.boot;

import com.spring4all.spring.boot.starter.hbase.aop.TimeKeepingAspect;
import com.spring4all.spring.boot.starter.hbase.api.HBaseTemplate;
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
@EnableConfigurationProperties(HBaseProperties.class)
@ConditionalOnClass(HBaseTemplate.class)
public class HBaseAutoConfiguration {

    private final HBaseProperties hbaseProperties;

    @Autowired
    public HBaseAutoConfiguration(HBaseProperties hbaseProperties) {
        this.hbaseProperties = hbaseProperties;
    }

    @Bean
    @ConditionalOnMissingBean(HBaseTemplate.class)
    public HBaseTemplate hbaseTemplate() {
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
        return new HBaseTemplate(configuration);
    }

    @Bean
    public TimeKeepingAspect timeKeepingAspect() {
        return new TimeKeepingAspect();
    }
}
