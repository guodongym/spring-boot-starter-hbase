package com.spring4all.spring.boot.starter.hbase.utils;

import com.google.common.collect.Maps;
import com.spring4all.spring.boot.starter.hbase.boot.HBaseProperties;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created on 2019/4/26.
 *
 * @author zhaogd
 */
public class BeanUtilsTest {

    @Test
    public void beanToMap() {
        final HBaseProperties properties = new HBaseProperties();
        properties.setQuorum("192.168.1.109:2181");

        final Map<String, Object> map = BeanUtils.beanToMap(properties);
        assertEquals("192.168.1.109:2181", map.get("quorum"));
        assertEquals(properties.getQuorum(), map.get("quorum"));

        try {
            BeanUtils.beanToMap(null);
        } catch (NullPointerException e) {
            return;
        }
        Assert.fail("expect NullPointerException ");
    }

    @Test
    public void mapToBean() {
        final HashMap<String, Object> map = Maps.newHashMap();
        map.put("scannerCaching", "55430");
        final HBaseProperties bean = new HBaseProperties();
        BeanUtils.mapToBean(map, bean);

        assertEquals("55430", bean.getScannerCaching());
        assertEquals(map.get("scannerCaching"), bean.getScannerCaching());


        try {
            BeanUtils.mapToBean(null, null);
        } catch (NullPointerException e) {
            return;
        }
        Assert.fail("expect NullPointerException ");
    }
}