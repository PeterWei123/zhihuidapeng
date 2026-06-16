package com.qkd.iotserver.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.qkd.iotserver.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

@Mapper//
public interface UserMapper  extends BaseMapper<User> {//接口里必须是抽象方法

    @Select("select * from tbl_user where username=#{username} and password=#{password}")
    User getUser(User user);

    @Override
    @Select("select * from tbl_user where status='租户' order by id desc")
    List<User> selectList(Wrapper<User> queryWrapper);
}
