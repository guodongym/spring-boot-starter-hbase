package com.spring4all.spring.boot.starter.hbase.page;

import lombok.Data;

/**
 * @author zhaogd
 * @date 2019/4/30
 */
@Data
public class Column {

    private String family;

    private String qualifier;
}
