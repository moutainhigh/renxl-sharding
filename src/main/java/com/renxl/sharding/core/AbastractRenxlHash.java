package com.renxl.sharding.core;

/**
 * 将多个sharding字段合并成sharding Columns 在映射到数值空间
 */
public abstract class AbastractRenxlHash implements IRenxlHash {
    @Override
    public int hashColumns(String shardingColumns) {

        return hashSharding(shardingColumns);
    }

    public abstract int hashSharding(String shardingColumns);
}
