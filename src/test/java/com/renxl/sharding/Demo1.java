package com.renxl.sharding;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;

public class Demo1 {

    public static void main(String[] args) {
        f1(Test.class);
    }

    private static <T> void f1(Class<T> cls) {
        Method[] methods = cls.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            method.setAccessible(true);
            Parameter[] parameters = method.getParameters();
            for (int j = 0; j < parameters.length; j++) {
                Parameter parameter = parameters[j];
                Annotation[] annotations = parameter.getDeclaredAnnotations();
                for (int k = 0; k < annotations.length; k++) {
                    Annotation annotation = annotations[k];
                    Class<? extends Annotation> aClass = annotation.annotationType();
                    Class<? extends Annotation> aClass1 = annotation.getClass();
                    Object fields = getAnnotationValue(annotation, "fields");
                    Object fields1 = getAnnotationValue(annotation, "value");
                    System.out.println(annotation);
                }
            }
        }
    }


    public static Object getAnnotationValue(Annotation annotation, String property) {
        Object result = null;
        if (annotation != null) {
            InvocationHandler invo = Proxy.getInvocationHandler(annotation); //获取被代理的对象
            Map map = (Map) getFieldValue(invo, "memberValues");
            if (map != null) {
                result = map.get(property);
            }
        }
        return result;
    }

    public static <T> Object getFieldValue(T object, String property) {
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