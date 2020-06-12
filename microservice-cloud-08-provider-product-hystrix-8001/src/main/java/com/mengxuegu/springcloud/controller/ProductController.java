package com.mengxuegu.springcloud.controller;

import com.mengxuegu.springcloud.entities.Product;
import com.mengxuegu.springcloud.service.ProductService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: 梦学谷
 */
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;
    //当get方法出现异常时，则会调用hystrixGet方法处理

    @RequestMapping(value = "/product/add", method = RequestMethod.POST)
    public boolean add(@RequestBody Product product) {
        return productService.add(product);
    }

    @HystrixCommand(fallbackMethod = "getFallBack")
    @RequestMapping(value = "/product/get/{id}", method = RequestMethod.GET)
    public Product get(@PathVariable("id") Long id) {
        Product product = productService.get(id);
        //模拟异常
        if(product == null) {
            throw new RuntimeException("ID=" + id + "无效");
        }
        return product;
    }
    //当get方法出现异常时，则会调用此方法. 注意此方法的返回值，参数列表与原方法一致
    public Product getFallBack(@PathVariable("id") Long id){
        return new Product(id,
                "ID=" + id + "无效--@HystrixCommand",
                "无有效数据库");
    }

    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    public List<Product> list() {
        return productService.list();
    }

}
