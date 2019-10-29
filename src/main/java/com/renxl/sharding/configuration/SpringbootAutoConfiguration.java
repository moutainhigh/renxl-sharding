package com.renxl.sharding.configuration;

import com.renxl.sharding.aop.RenxlRouteAdvisor;
import com.renxl.sharding.aop.RenxlRouteInterceptor;
import com.renxl.sharding.core.*;
import com.renxl.sharding.plugin.ShardTablePlugin;
import com.renxl.sharding.route.DefaultHash;
import com.renxl.sharding.route.DefaultRoute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Slf4j
@Configuration
@EnableConfigurationProperties(SpringBootAutoConfProperties.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class SpringbootAutoConfiguration {

    @Autowired
    private SpringBootAutoConfProperties autoConfProperties;
    @Bean
    @ConditionalOnMissingBean
    public ShardTablePlugin getInterceptor(){
        log.info("==> 加载分表拦截器");
        return new ShardTablePlugin();
    }

    @Bean
    @ConditionalOnMissingBean
    public IRenxlHash getIRenxlHash(){
        log.info("==> 加载hash路由");
        return new DefaultHash();
    }



    @Bean
    @ConditionalOnMissingBean
    public IRenxlRoute getIRenxlRoute(){
        log.info("==> 加载默认路由");
        return new DefaultRoute();
    }


    @Bean
    @ConditionalOnMissingBean
    public RenxlRouteAdvisor getRenxlRouteAdvisor(){
        log.info("==> 分库分表主组件");
        RenxlRouteAdvisor renxlRouteAdvisor = new RenxlRouteAdvisor();
        renxlRouteAdvisor.setAdvice(getRenxlRouteInterceptor());
        return renxlRouteAdvisor;
    }


    @Bean
    @ConditionalOnMissingBean
    public RenxlRouteInterceptor getRenxlRouteInterceptor(){
        log.info("==> 分库分表主执行器");
        RenxlRouteInterceptor renxlRouteInterceptor = new RenxlRouteInterceptor();
        renxlRouteInterceptor.setIRenxlHash(getIRenxlHash());
        renxlRouteInterceptor.setIRenxlRoute(getIRenxlRoute());
        return renxlRouteInterceptor;
    }



    @Bean
    @ConditionalOnMissingBean
    public RenxlMultipleDatasource getRenxlMultipleDatasource(){
        log.info("==> 加载分库分表多数据源");
        RenxlMultipleDatasource renxlMultipleDatasource = new RenxlMultipleDatasource();
        return renxlMultipleDatasource;
    }


    @Bean
    @ConditionalOnMissingBean
    public RenxlConfiguration getRenxlConfiguration(@Autowired RenxlDataSource renxlDataSource){
        log.info("==> 加载分库分表组件配置");
        RenxlConfiguration renxlMultipleDatasource = new RenxlConfiguration();
        renxlMultipleDatasource.setRenxlDataSource(renxlDataSource);
        renxlMultipleDatasource.setAlias(new ArrayList<>(renxlDataSource.getDataSources().keySet()));
        renxlMultipleDatasource.setSplitSymbol(autoConfProperties.getSplitSymbol());
        renxlMultipleDatasource.setTableSize(autoConfProperties.getTableSize());
        return renxlMultipleDatasource;
    }






}
