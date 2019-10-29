package com.renxl.sharding.core;

import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 多数剧源抽象类
 */
public abstract class RenxlDataSource extends AbstractDataSource {

    /**
     * 多数据源 别名和数据源集合
     */
    protected  Map<String,DataSource> dataSources;
    /**
     * 多数据源别名列表 索引对应route结果 ,别名对应 #{dataSources}的String
     */
    protected  List<String> indexMappingAlias;




    @Override
    public Connection getConnection() throws SQLException {
        return determineDatasource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineDatasource().getConnection(username,password);
    }

    public abstract DataSource determineDatasource();
}
