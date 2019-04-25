package com.spring4all.spring.boot.starter.hbase.boot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * date： 2016-11-16 14:51:42
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.data.hbase")
class HbaseProperties {

    private String quorum;

    /**
     * HBase scanner一次从服务端抓取的数据条数
     */
    private String scannerCaching = "1000";

    /**
     * scan超时时间(MS)
     */
    private String scannerTimeoutPeriod = "30000";

    /**
     * RPC超时时间(MS)
     */
    private String rpcTimeout = "30000";

    /**
     * 重试次数
     */
    private String retriesNumber = "5";

    /**
     * 连接池默认核心连接数
     */
    private String connectionThreadsCore = "32";

    private String rootDir = "/hbase";

    private String nodeParent = "/hbase";


    /**
     * Additional properties used to configure the client.
     */
    private Map<String, String> properties = new HashMap<>();
}
