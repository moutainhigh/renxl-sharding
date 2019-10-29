package com.renxl.sharding.core;

import com.renxl.sharding.holder.DatasourceHolder;

import javax.sql.DataSource;

/**
 * 获取动态数据源
 */
public class RenxlMultipleDatasource  extends RenxlDataSource{
    @Override
    public DataSource determineDatasource() {
        String alias = DatasourceHolder.peek();
        DataSource dataSource = this.dataSources.get(alias);
        return dataSource;
    }
}
