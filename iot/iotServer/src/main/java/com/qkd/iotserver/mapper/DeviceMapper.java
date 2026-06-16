package com.qkd.iotserver.mapper;




import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qkd.iotserver.pojo.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {

    @Override
    @Select("select * from v_device order by id desc ")
    List<Device> selectList(Wrapper<Device> queryWrapper);

    @Select("select * from v_device where uid=#{uid} order by id desc")
    List<Device> mydev(@Param("uid") int uid);

    @Select("select * from v_device where pid=#{pid} order by id desc")
    List<Device> provdev(@Param("pid") int pid);

}