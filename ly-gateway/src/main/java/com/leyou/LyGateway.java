package com.leyou;


import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

//@SpringBootApplication
//@EnableEurekaClient      //官方推荐如果注册中心是eureka，尽量用
//@EnableDiscoveryClient   //注册中心可以采用zookeeper
//@EnableCircuitBreaker

@SpringCloudApplication
@EnableZuulProxy   //开启zuul的网关功能
public class LyGateway {

    public static void main(String[] args) {
        SpringApplication.run(LyGateway.class,args);
    }
}
