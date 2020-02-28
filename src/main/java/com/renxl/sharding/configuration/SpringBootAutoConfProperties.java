package com.renxl.sharding.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Slf4j
@Data
@ConfigurationProperties(prefix = "renxl.multiple")
public class SpringBootAutoConfProperties {

    private List<String> alias;

    private String splitSymbol;

    Integer tableSize;
}
