package com.mengxuegu.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Auther: 梦学谷
 */
@Configuration //标识配置类
public class ConfigBean {

    @LoadBalanced//表示RestTemplate开启负载均衡
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
