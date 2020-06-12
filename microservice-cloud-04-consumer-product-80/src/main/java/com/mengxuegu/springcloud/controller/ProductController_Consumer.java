package com.mengxuegu.springcloud.controller;

import com.mengxuegu.springcloud.entities.Product;
import com.mengxuegu.springcloud.service.AutoIdempotent;
import com.mengxuegu.springcloud.service.RedisService;
import com.mengxuegu.springcloud.service.TokenService;
import com.mengxuegu.springcloud.service.impl.QiangDanServiceImpl;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Auther: 梦学谷
 */
@RestController
public class ProductController_Consumer {

    //private static final String REST_URL_PREFIX = "http://localhost:8001";
    private static final String REST_URL_PREFIX = "http://microservice-product";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private QiangDanServiceImpl qiangDanServiceImpl;

    @RequestMapping(value = "/consumer/product/add")
    public boolean add(@RequestBody Product product) {
        return restTemplate.postForObject(REST_URL_PREFIX + "/product/add", product, Boolean.class);
    }

    @RequestMapping(value = "/consumer/product/get/{id}")
    public Product get(@PathVariable("id") Long id) {
        return restTemplate.getForObject(REST_URL_PREFIX + "/product/get/" + id, Product.class);
    }

    @RequestMapping(value = "/consumer/product/list")
    public List<Product> list() {
        return restTemplate.getForObject(REST_URL_PREFIX + "/product/list", List.class);
    }

    @PostMapping("/get/token")
    public String getToken(){
        String token = tokenService.createToken();
        if(!(StringUtils.isEmpty(token))){
            return "获取token:"+token+"成功";
        }
        return "获取token:"+token+"失败";
    }

    /**
     * 测试幂等
     * @return
     */
    @AutoIdempotent
    @PostMapping("/test/idempotence")
    public List<Product> testIdempotence(){
        return restTemplate.getForObject(REST_URL_PREFIX + "/product/list", List.class);
    }

    /**
     * 测试redis分布式锁
     * @param key
     * @param value
     * @return
     */
    @GetMapping("/stnx/{key}/{value}")
    public boolean setnx(@PathVariable String key,@PathVariable String value){
        return redisService.setnx(key,value);
    }

    /**
     * 测试删除锁
     * @param key
     * @param value
     * @return
     */
    @GetMapping("/delnx/{key}/{value}")
    public int delnx(@PathVariable String key,@PathVariable String value){
        return redisService.delnx(key,value);
    }

    /**
     * 模拟抢单
     * @return
     */
    @GetMapping("/qiangdan")
    public List<String> qiangdan(){
        return qiangDanServiceImpl.qiangdan();
    }
}
