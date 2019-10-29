package com.renxl.sharding.core;

/**
 * 将业务字段映射到指定的hash值
 *
 *
 * 并交给route 进行路由用来获取相关裤的下标 存储到DataSourceHolder
 */
public interface IRenxlHash {

    int hashColumns(String Columns);



}
