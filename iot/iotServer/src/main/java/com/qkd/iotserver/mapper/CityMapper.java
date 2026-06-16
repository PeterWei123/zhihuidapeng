package com.qkd.iotserver.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qkd.iotserver.pojo.City;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 把这个组件放入到Spring中
public interface CityMapper extends BaseMapper<City> {
    // 继承一下BaseMapper，也就意味着增删改查SQL语句就写完了
}