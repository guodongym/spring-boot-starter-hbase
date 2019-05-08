package com.spring4all.spring.boot.starter.hbase.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaogd
 * @date 2019/4/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Column {

    private String family;

    private String qualifier;
}
