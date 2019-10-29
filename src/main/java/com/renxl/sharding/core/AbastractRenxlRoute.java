package com.renxl.sharding.core;

import com.renxl.sharding.holder.DatasourceHolder;
import com.renxl.sharding.holder.TableHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 经hash映射到数值空间的值 经过路由算法后存储到DatasourceHolder 以及tableSourceHolder
 */
public abstract class AbastractRenxlRoute implements IRenxlRoute {

    protected  RenxlConfiguration renxlConfiguration;

    protected abstract int routeDb(int hashId);

    protected abstract int routeTable(int hashId);



    public void route(String defaultDbAlias,int hashId, List<String> logictables, List<String> excludeTables) {
        if(defaultDbAlias !=null && !"".equals(defaultDbAlias)){
            // 存在默认的路由则选择默认的路由
            DatasourceHolder.push(defaultDbAlias);
        }else {
            // 不存在默认的路由则根据分库分表字段的数据域进行路由
            int shardDb  = routeDb(hashId);
            String alias = getAlias(shardDb);
            DatasourceHolder.push(alias);
        }

        int shardtable  = routeTable(hashId);
        // 添加路由表集合到线程变量
        List<String> realTables = getRealTables(shardtable, logictables, excludeTables, renxlConfiguration.splitSymbol);
        // realTables大小为0则不进行分库分表
        TableHolder.push(realTables);
    }

    /**
     * 将逻辑表拼装成真实表|路由表
     * 拼接规则：逻辑表名 + split_symbol + #{路由结果}
     * @param shardtable
     * @param logictables
     * @param excludeTables
     * @param split_symbol
     * @return
     */
    protected List<String> getRealTables(int shardtable, List<String> logictables, List<String> excludeTables, String split_symbol){
        List<String> realTables = new ArrayList<>();
        if(excludeTables!=null && excludeTables.size() > 0){
            for(String logictable:logictables){
                if(excludeTables.contains(logictable)){
                    continue;
                }
                realTables.add(logictable+split_symbol+shardtable);
            }
        } else {
            for(String logictable:logictables){
                realTables.add(logictable+split_symbol+shardtable);
            }
        }
        return realTables;
    }


    /**
     * 根据路由结果获取配置的DB别名
     * @param shardDb
     * @return
     */
    protected String getAlias(int shardDb){
        return renxlConfiguration.alias(shardDb);

    };
}
