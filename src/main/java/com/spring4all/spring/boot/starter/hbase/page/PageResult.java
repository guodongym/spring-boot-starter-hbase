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
public class PageResult<T> {

    private Long totalCount;

    private String pageStartKey;

    private String pageStopKey;

    private List<T> data;
}
