package com.renxl.sharding.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RenxlConfiguration {

    /**
     * 分表与下标的连接符
     */
    String splitSymbol;
    /**
     * 多数据源别名列表 索引对应route结果 ,别名对应 #{dataSources}的String
     */
    List<String> alias;
    /**
     * 多数据源
     */
    RenxlDataSource renxlDataSource;
    /**
     * 每张逻辑表分成几个真实表
     */
    Integer tableSize;


    public String alias(int shardDb) {
        return alias.get(shardDb);
    }

    public int dbSize() {
        return alias.size();
    }

    public int tableSize() {
        return tableSize;
    }
}
