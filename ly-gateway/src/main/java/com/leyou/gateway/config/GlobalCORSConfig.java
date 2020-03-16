package com.leyou.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CORS跨域配置类
 */
@Configuration   //相当于xml配置文件
@EnableConfigurationProperties(CORSProperties.class)
public class GlobalCORSConfig {


    @Bean
    public CorsFilter getCorsFilter(CORSProperties prop){
        //        1.添加cors的配置信息
        CorsConfiguration config = new CorsConfiguration();
        //          允许访问的域
        //        List<String> list = prop.getAllowedOrigins();
        //        for (String origin : list) {
        //            config.addAllowedOrigin(origin);
        //        }
        prop.getAllowedOrigins().forEach(config::addAllowedOrigin);

        //          是否允许发送cookie
        //config.setAllowCredentials(true);
        config.setAllowCredentials(prop.getAllowedCredentials());

        //          允许的请求方式
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("PUT");
//        config.addAllowedMethod("DELETE");
        prop.getAllowedMethods().forEach(config::addAllowedMethod);


        //          允许的头信息
        //config.addAllowedHeader("*");
        prop.getAllowedHeaders().forEach(config::addAllowedHeader);

        //          访问有效期
        //config.setMaxAge(3600L);
        config.setMaxAge(prop.getMaxAge());

        //       2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //source.registerCorsConfiguration("/**", config);
        source.registerCorsConfiguration(prop.getFilterPath(), config);

        //       3.返回新的CORSFilter
        return new CorsFilter(source);
    }
}
