package com.renxl.sharding.holder;

import java.util.ArrayDeque;
import java.util.List;

/**
 * holder业务key产生的路由信息
 * 存放在线程变量中
 * 供datasource 分库
 * ArrayDeque解决嵌套问题
 */
public class DatasourceHolder {
    /**
     * 非阻塞结构 db 存储数据库对应的别名
     */
    public static ThreadLocal<ArrayDeque<String>> db = new ThreadLocal(){
        protected Object initialValue() {
            return new ArrayDeque();
        }
    } ;

    /**
     *
     * @param alias
     */
    public static void push(String alias) {
        if(alias == null){
            alias = "";   // 保持对称结构 并且获取默认的数据源
        }
        db.get().push(alias);
    }

    /**
     * 获取弹栈 非阻塞 不报错
     *
     * @return
     */
    public static String poll() {
        String poll = db.get().poll();
        // pop栈顶的元素， 当栈中没有元素时，调用该方法会发生异常
        // return db.get().pop();

        if(db.get().isEmpty()){
            db.remove();
        }
        return poll;
    }

    /**
     * 获取不弹栈
     *
     * @return
     */
    public static String peek() {
        return db.get().peek();
    }
}
