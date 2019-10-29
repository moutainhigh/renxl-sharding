package com.renxl.sharding.route;

import com.renxl.sharding.core.AbastractRenxlRoute;

/**
 * 取余算法
 * 推荐初始表数量为2的n次方 方便平滑扩容
 */
public class DefaultRoute extends AbastractRenxlRoute {
    /**
     *
     * @param hashId 需要分库分表的字符串字段映射出的数值
     * @return 分库尾缀
     */
    @Override
    protected int routeDb(int hashId) {
        return hashId % renxlConfiguration.dbSize();
    }

    /**
     *
     * @param hashId 需要分库分表的字符串字段映射出的数值
     * @return 分表尾缀
     */
    @Override
    protected int routeTable(int hashId) {
        return hashId % renxlConfiguration.tableSize();
    }


}
