package com.qkd.iotserver.controller;


import com.qkd.iotserver.mapper.CategoryMapper;
import com.qkd.iotserver.pojo.Category;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController extends HttpController<CategoryMapper, Category>{
}
