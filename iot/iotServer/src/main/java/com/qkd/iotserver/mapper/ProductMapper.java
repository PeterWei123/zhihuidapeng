package com.qkd.iotserver.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qkd.iotserver.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    //需要对BaseMapper中的selectList重写,改写成查询视图


    @Override
    @Select("select * from v_product order by id desc ")
    List<Product> selectList(Wrapper<Product> queryWrapper);

    @Select("select * from v_product where uid=#{uid} order by id desc")
    List<Product>mypro(@Param("uid") int uid);
    //当形参为基本数据类型或String时，并且类是接口时，编译器在编译时
    //会把形参的参数名偷偷的改名

    @Select("select * from v_product where devip Like '${devip}%'")
    Product getLike(@Param("devip")String devip);
}