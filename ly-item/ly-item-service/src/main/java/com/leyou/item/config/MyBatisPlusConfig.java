package com.leyou.item.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  //xml文件
public class MyBatisPlusConfig {

    //将分页插件创建交给spring容器
    @Bean //<bean></bean>
    public PaginationInterceptor getPaginationInterceptor(){
        return new PaginationInterceptor();
    }
}
