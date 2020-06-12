package com.mengxuegu.springcloud.service;

import com.mengxuegu.springcloud.entities.Product;
import com.mengxuegu.springcloud.service.impl.ProductClientServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
@FeignClient(value = "MICROSERVICE-PRODUCT", fallback =
        ProductClientServiceFallBack.class)
public interface ProductClientService {
    /**
     * @Auther: 梦学谷
     */
    //指定调用的服务 MICROSERVICE-PRODUCT
    @RequestMapping(value = "/product/get/{id}",method = RequestMethod.GET)
    Product get(Long id);
    @RequestMapping(value = "/product/list",method = RequestMethod.GET)
    List<Product> list();
    @RequestMapping(value = "/product/add",method = RequestMethod.POST)
    boolean add(Product product);
    }
