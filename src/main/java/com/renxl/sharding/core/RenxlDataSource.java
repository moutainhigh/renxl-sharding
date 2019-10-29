package com.renxl.sharding.core;

import lombok.Data;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 多数剧源抽象类
 */
@Data
public abstract class RenxlDataSource extends AbstractDataSource {

    /**
     * 多数据源 别名和数据源集合
     */
    protected  Map<String,DataSource> dataSources;




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
