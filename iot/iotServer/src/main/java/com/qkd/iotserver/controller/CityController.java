package com.qkd.iotserver.controller;


import com.qkd.iotserver.mapper.CityMapper;
import com.qkd.iotserver.pojo.City;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/city")
public class CityController extends HttpController<CityMapper, City>{
}
