package com.qkd.iotserver.controller;


import com.qkd.iotserver.mapper.ProductMapper;
import com.qkd.iotserver.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/product")
public class ProductController extends HttpController<ProductMapper, Product> {
    @Autowired
    ProductMapper productMapper;

    @RequestMapping("/pro")
    public List<Product> pro(int uid) {

        System.out.println("当前用户id为"+uid);
        return productMapper.mypro(uid);
    }
}