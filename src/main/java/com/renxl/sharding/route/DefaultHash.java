package com.renxl.sharding.route;

import com.renxl.sharding.core.AbastractRenxlHash;
import com.renxl.sharding.core.IRenxlHash;

import static java.lang.System.identityHashCode;

/**
 * 将需要分库分表的字符串集合 映射到数值空间 从而交给路由算法进行路由
 */
public class DefaultHash extends AbastractRenxlHash {

    /**
     *
     * @param shardingColumns 需要分库分表的多个字符串的拼接字符串
     * @return
     */
    @Override
    public int hashSharding(String shardingColumns) {

        return shardingColumns.hashCode();
    }



}
