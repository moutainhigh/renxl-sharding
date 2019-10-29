package com.renxl.sharding.core;

import java.util.List;

/**
 * 返回路由信息
 */
public interface IRenxlRoute {

    void route(String defaultDbAlias, int hashId, List<String> logictables, List<String> excludeTables);
}
