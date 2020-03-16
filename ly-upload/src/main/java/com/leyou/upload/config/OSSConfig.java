package com.leyou.upload.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//设置阿里云的客户端oss对象
@Configuration
public class OSSConfig {
    @Bean//放入ioc容器
    public OSS ossClient(OSSProperties prop){
        return new OSSClientBuilder()
                .build(prop.getEndpoint(),
                        prop.getAccessKeyId(),
                        prop.getAccessKeySecret());
    }

}
