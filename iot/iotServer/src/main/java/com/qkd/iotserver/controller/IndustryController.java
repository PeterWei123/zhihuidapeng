package com.qkd.iotserver.controller;


import com.qkd.iotserver.mapper.IndustryMapper;
import com.qkd.iotserver.pojo.Industry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/industry")
public class IndustryController extends HttpController<IndustryMapper, Industry>{

}
