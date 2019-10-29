package com.renxl.sharding.util;

public class ClassTypeUtil {

    public static boolean isBasicType(Object obj) {
        if(obj instanceof String){
            return true;
        }
        try {
            return ((Class<?>)obj.getClass().getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

}
