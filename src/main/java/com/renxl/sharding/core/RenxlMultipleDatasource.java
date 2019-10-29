package com.renxl.sharding.core;

import com.renxl.sharding.holder.DatasourceHolder;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * 获取动态数据源
 */
@Data
public class RenxlMultipleDatasource  extends RenxlDataSource  {
    @Override
    public DataSource determineDatasource() {
        String alias = DatasourceHolder.peek();
        DataSource dataSource = this.dataSources.get(alias);
        return dataSource;
    }


}
