package com.renxl.sharding.util;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.util.Properties;

public abstract class PluginUtils {
    public static final String DELEGATE_BOUNDSQL_SQL = "delegate.boundSql.sql";

    private PluginUtils() {
    }

    public static Object realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        } else {
            return target;
        }
    }

    public static String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        return StringUtils.isEmpty(value) ? null : value;
    }
}
