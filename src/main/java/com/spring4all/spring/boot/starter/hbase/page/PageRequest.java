package com.spring4all.spring.boot.starter.hbase.page;

import lombok.Data;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author zhaogd
 * @date 2019/4/30
 */
@Data
public class PageRequest {

    private boolean asc = true;

    private Boolean isNext = null;

    private Integer pageSize = 10;

    private String pageFirstRowKey;

    private String pageLastRowKey;

    private String startRow;

    private String stopRow;

    private List<Column> columns;
}
