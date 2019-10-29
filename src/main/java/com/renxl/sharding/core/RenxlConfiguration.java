package com.renxl.sharding.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RenxlConfiguration {

    /**
     * 分表与下标的连接符
     */
    String split_symbol;
    /**
     * 数据库别名
     */
    List<String> alias;

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
