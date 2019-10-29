package com.renxl.sharding.plugin;

import com.renxl.sharding.core.RenxlConfiguration;
import com.renxl.sharding.holder.TableHolder;
import com.renxl.sharding.util.PluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 分表原理:
 * mybatis 工作流程
 * mapper-> proxyclass-> opensession-> executor (getConn) -> statementHandler(parameterHandler resultHandler)
 * 通过mapper接口对应代理类执行SQL语句 过程中会获取Conn [我们会在该代理执行前设置线程变量中的分库分表信息]
 * conn 会从RenxlDataSource获取；从而完成分库
 * <p>
 * statementHandler在之前前 我们通过Plugin代理原代理
 * 并且获取最终的执行语句
 * 在交给jdbc执行前
 * 我们通过TableHolder中的真实表替换所有的非真实表
 * 从而达到分表效果
 * <p>
 * 注： RenxlRouteAdvisor aop优先级最高 所以代理执行前设置线程变量中的分库分表信息
 */

@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
)})
@Slf4j
public class ShardTablePlugin implements Interceptor {

    private RenxlConfiguration renxlConfiguration;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        List<String> realTables = TableHolder.peek();
        if (!CollectionUtils.isEmpty(realTables)) {
            StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
            MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            String sql = boundSql.getSql();
            String mSql = sql;
            List<String> interceptorsAfter = new ArrayList<>();
            List<String> interceptorsBefore = new ArrayList<>();
            // 自定义hash一致性分库分表位置: 通过hash一致算法路由之后的数据
            mSql =" " +  mSql + " ";
            for (String realTable : realTables) {
                int i = realTable.lastIndexOf(renxlConfiguration.getSplit_symbol());
                String logicTable = realTable.substring(0, i);
                StringBuffer logicTableSb = new StringBuffer(" ").append(logicTable).append(" ");
                StringBuffer realTableSb = new StringBuffer(" ").append(realTable).append(" ");
                mSql = mSql.replaceAll(logicTableSb.toString(), realTableSb.toString());
                if (mSql.contains(realTable)) {
                    interceptorsAfter.add(realTable);
                    interceptorsBefore.add(logicTable);
                }
            }
            if (!CollectionUtils.isEmpty(interceptorsBefore)) {
                log.info("ShardTablePlugin: 拦截开始,表名替换如下interceptorsBefore =>{},interceptorsAfter=>{}", Arrays.toString(interceptorsBefore.toArray()), Arrays.toString(interceptorsAfter.toArray()));
            }
            //通过反射修改sql语句
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, mSql);
        }

        // 传递给下一个拦截器处理
        return invocation.proceed();
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

}