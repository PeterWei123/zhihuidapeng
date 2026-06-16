package com.qkd.iotserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.qkd.iotserver.pojo.Datadict;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DatadictMapper extends BaseMapper<Datadict> {
    @Select("select * from tbl_datadict where mykey=#{mykey}")
    String getValue(@Param("mykey")String mykey);

    @Update("update tbl_datadict set value=#{value} where mykey=#{mykey}")
    void setValue(Datadict datadict);

}
