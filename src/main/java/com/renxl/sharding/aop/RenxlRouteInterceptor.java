package com.renxl.sharding.aop;


import com.renxl.sharding.annotation.RenxlRoute;
import com.renxl.sharding.annotation.RenxlRouteId;
import com.renxl.sharding.core.IRenxlHash;
import com.renxl.sharding.core.IRenxlRoute;
import com.renxl.sharding.holder.DatasourceHolder;
import com.renxl.sharding.holder.TableHolder;
import com.renxl.sharding.util.AnnocationProxyUtil;
import com.renxl.sharding.util.ClassTypeUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class RenxlRouteInterceptor implements MethodInterceptor {
    private String priority_level="order";
    @Getter
    @Setter
    private IRenxlRoute iRenxlRoute;
    @Getter
    @Setter
    private IRenxlHash iRenxlHash;

    /**
     * 分库分表拦截器
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            // 核心流程 通过route函数进行分库分表，并将分库分表信息存储在线程变量 供给 mybatis session
            Object[] arguments = invocation.getArguments();

            if(arguments == null ){
                return invocation.proceed();
            }
            RenxlRoute annotation = invocation.getMethod().getAnnotation(RenxlRoute.class);
            if(annotation == null ){
                return invocation.proceed();
            }
            String[] logictables = annotation.logictables();
            String[] excludesTables = annotation.excludesTables();

            // 获取分库分表聚合字段
            TreeMap<Integer, List<String>> shardingColumnsPriority = new TreeMap<>();
            initShardingColumnsPriority(invocation, arguments, shardingColumnsPriority);
            // sharding 分库分表聚合字段
            String shardingColumnsString = initShardingColumns(shardingColumnsPriority);
            // 映射到数值域
            if("".equals(shardingColumnsString)){
                // 未获取到指定的路由字段则不进行分库分表
                return invocation.proceed();
            }
            int shardingColumnHash = iRenxlHash.hashColumns(shardingColumnsString);
            // 处理路由 设置到线程变量  进行分库 判断有没有设置默认库 有参数有注解的情况下才进行分库分表
            String defaultDb = annotation.defaultDb();
            iRenxlRoute.route(defaultDb,shardingColumnHash,logictablestoList(logictables),excludesTablesToList(excludesTables));
            return invocation.proceed();
        }  finally {
            // 移除线程数据 防止内存泄漏
            DatasourceHolder.poll();
            TableHolder.poll();
        }
    }

    private List<String> excludesTablesToList(String[] excludesTables) {
        if(excludesTables == null || excludesTables.length == 0){
            return new ArrayList<>();
        }
        return Arrays.asList(excludesTables);
    }

    private List<String> logictablestoList(String[] logictables) {
        if(logictables == null || logictables.length == 0){
            return new ArrayList<>();
        }
        return Arrays.asList(logictables);
    }

    /**
     * 将分库分表字段值集合按优先级聚合和聚合字段
     * @param shardingColumnsPriority
     * @return
     */
    private String initShardingColumns(TreeMap<Integer, List<String>> shardingColumnsPriority) {
        StringBuffer shardingColumns = new StringBuffer("");
        Set<Map.Entry<Integer, List<String>>> entries = shardingColumnsPriority.entrySet();
        for( Map.Entry<Integer, List<String>> entry : entries){
            List<String> values = entry.getValue();
            for(String value : values){
                shardingColumns.append(value);
            }
        }
        return shardingColumns.toString();
    }

    /**
     * 根据{@code RenxlRouteId}获取需要进行分库分表的字段 并且按照{@code RenxlRouteId}的优先级进行排序后
     * 根据排序生成聚合字段
     * 对聚合字段进行hash到数值空间
     * 根据hash结果以及路由函数进行分库分表
     * @param invocation
     * @param arguments
     * @param shardingColumnsPriority
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void initShardingColumnsPriority(MethodInvocation invocation, Object[] arguments, TreeMap<Integer, List<String>> shardingColumnsPriority) throws NoSuchFieldException, IllegalAccessException {
        Annotation[][] parameterAnnotations = invocation.getMethod().getParameterAnnotations();
        //参数注解，1维是参数，2维是注解
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Object param = arguments[i];
            Annotation[] paramAnn = parameterAnnotations[i];
            // 基本类型
            if(ClassTypeUtil.isBasicType(param)){
                Annotation have = haveRenxlRouteIdAnnotation(paramAnn);
                if(have!=null){
                    int priorityValue = (Integer) AnnocationProxyUtil.getAnnotationValue(have, priority_level);
                    String shardingColumn = String.valueOf(param);
                    putPriorityValue(shardingColumnsPriority, shardingColumn, priorityValue);
                }
            }else {
                // 聚合类型
                Field[] declaredFields = param.getClass().getDeclaredFields();
                for (Field declaredField: declaredFields){
                    declaredField.setAccessible(true);
                    boolean annotationPresent = declaredField.isAnnotationPresent(RenxlRouteId.class);
                    if(annotationPresent){
                        RenxlRouteId aggParamAnnocation = declaredField.getAnnotation(RenxlRouteId.class);
                        String shardingColumn = String.valueOf(declaredField.get(param));
                        int priorityValue = aggParamAnnocation.order();
                        putPriorityValue(shardingColumnsPriority, shardingColumn, priorityValue);
                    }
                }
            }
        }
    }

    private void putPriorityValue(TreeMap<Integer, List<String>> shardingColumnsPriority, Object param, int priorityValue) {
        List<String> strings = shardingColumnsPriority.get(priorityValue);
        if(strings == null ){
            strings = new ArrayList<>();
        }
        strings.add(String.valueOf(strings));
        shardingColumnsPriority.put(priorityValue,strings);
    }

    private Annotation haveRenxlRouteIdAnnotation(Annotation[] paramAnn) throws NoSuchFieldException {
        for (Annotation annotation_i : paramAnn) {
            if(annotation_i.annotationType().equals(RenxlRouteId.class)){

                //
                Field order = annotation_i.getClass().getField("");
                return annotation_i;
            }
        }
        return null;
    }
}
