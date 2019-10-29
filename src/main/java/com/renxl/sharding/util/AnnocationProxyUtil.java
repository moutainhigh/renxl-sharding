package com.renxl.sharding.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

public class AnnocationProxyUtil {
    private static final String PROXY_PROPERTIES = "memberValues";

    public static Object getAnnotationValue(Annotation annotation, String property) {
        Object result = null;
        if (annotation != null) {
            InvocationHandler invo = Proxy.getInvocationHandler(annotation); //获取被代理的对象
            Map map = (Map) getFieldValue(invo, PROXY_PROPERTIES);
            if (map != null) {
                result = map.get(property);
            }
        }
        return result;
    }

    private static <T> Object getFieldValue(T object, String property) {
        if (object != null && property != null) {
            Class<T> currClass = (Class<T>) object.getClass();

            try {
                Field field = currClass.getDeclaredField(property);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(currClass + " has no property: " + property);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
