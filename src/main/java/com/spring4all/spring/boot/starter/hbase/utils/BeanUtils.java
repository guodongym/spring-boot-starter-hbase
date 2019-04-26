package com.spring4all.spring.boot.starter.hbase.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

/**
 * Bean操作工具类，Bean的拷贝，与Map转换
 *
 * @author zhaogd
 * @date 2019/4/26
 */
public class BeanUtils {

    /**
     * 将javaBean装换为map
     *
     * @param bean javaBean
     * @return 转换后的Map
     */
    public static Map<String, Object> beanToMap(Object bean) {
        Preconditions.checkNotNull(bean, "bean对象不能为空");

        BeanMap beanMap = BeanMap.create(bean);
        final Map<String, Object> map = Maps.newHashMap();
        for (Object key : beanMap.keySet()) {
            map.put(String.valueOf(key), beanMap.get(key));
        }
        return map;
    }

    /**
     * map中的值拷贝到javaBean
     *
     * @param map  待拷贝的map
     * @param bean 目标bean
     * @return 拷贝完成的bean
     */
    public static <T> void mapToBean(Map<String, Object> map, T bean) {
        Preconditions.checkNotNull(map, "map不能为空");
        Preconditions.checkNotNull(bean, "bean对象不能为空");

        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
    }
}
