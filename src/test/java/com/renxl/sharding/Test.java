package com.renxl.sharding;

public class Test {
    public static void test(@NoNull( "asda") String str) {
        if (str == null){
            System.out.println("拦截失败");
        }
        System.out.println("拦截成功");
    }
}