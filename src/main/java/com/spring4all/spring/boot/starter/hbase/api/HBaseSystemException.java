package com.spring4all.spring.boot.starter.hbase.api;

/**
 * HBase Data Access exception.
 *
 * @author Costin Leau
 */

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * desc： copy from spring data hadoop hbase, modified by JThink
 * date： 2016-11-15 16:08:41
 */
public class HBaseSystemException extends RuntimeException {

    public HBaseSystemException(Exception cause) {
        super(cause.getMessage(), cause);
    }

    public HBaseSystemException(Throwable throwable) {
        super(throwable.getMessage(), throwable);
    }
}
