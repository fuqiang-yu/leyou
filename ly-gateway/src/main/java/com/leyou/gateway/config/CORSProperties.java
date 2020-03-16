package com.leyou.gateway.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 通过配置类加载yml中的属性
 */
@Data
@ConfigurationProperties("ly.cors")
public class CORSProperties {

    private List<String> allowedOrigins; //属性名和yml中的属性要一致
    private Boolean allowedCredentials;
    private List<String> allowedHeaders;
    private List<String> allowedMethods;
    private Long maxAge;
    private String filterPath;

}
