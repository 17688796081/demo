package com.mengxuegu.springcloud.controller;

import com.mengxuegu.springcloud.entities.Product;
import com.mengxuegu.springcloud.service.ProductClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    private ProductClientService service;

    @RequestMapping(value = "/consumer/product/add")
    public boolean add(@RequestBody Product product) {
        return service.add(product);
    }

    @RequestMapping(value = "/consumer/product/get/{id}")
    public Product get(@PathVariable("id") Long id) {
        return service.get(id);
    }

    @RequestMapping(value = "/consumer/product/list")
    public List<Product> list() {
        return service.list();
    }



}
