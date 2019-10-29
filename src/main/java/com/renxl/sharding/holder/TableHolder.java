package com.renxl.sharding.holder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * holder业务key产生的路由信息
 * 存放在线程变量中
 * 供datasource 分库
 * ArrayDeque解决嵌套问题
 */
public class TableHolder {
    /**
     *  非阻塞结构 tables 真实表名
     */
    public static ThreadLocal<ArrayDeque<List<String>>> tables = new ThreadLocal(){
        protected Object initialValue() {
            return new ArrayDeque();
        }
    };

    /**
     * 子线程并非一定在当前上下文中创建;考虑之后各种情况比较复杂,子线程分库分表交给子线程自己实现
     * @param realtables
     */
    // public static InheritableThreadLocal<ArrayDeque<List<String>>> tables = new InheritableThreadLocal<>();// 不考虑子线程情况

    public static void push(List<String> realtables){
        if(realtables == null ){
            realtables = new ArrayList<>();   // 保持对称结构
        }
//        if(tables.get() == null){
//            ArrayDeque<List<String>> deque =new ArrayDeque();
//            tables.set(deque);
//        }
        tables.get().push(realtables);
    }

    /**
     * 获取不弹栈 非阻塞
     * @return
     */
    public static List<String>  poll(){
        List<String> poll = tables.get().poll();

        if(tables.get().isEmpty()){
            tables.remove();
        }
        return poll;
    }

    /**
     * 获取不弹栈
     * @return
     */
    public static List<String>  peek(){
        return tables.get().peek();
    }
}
