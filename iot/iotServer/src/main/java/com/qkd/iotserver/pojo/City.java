package com.qkd.iotserver.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qkd.iotserver.util.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
@TableName("tbl_city") // 让这个类和tbl_city表对应
public class City {
    @TableId(type = IdType.AUTO)
    public Integer id;
    @NotNull //不能为空
    public String city;
    @NotNull
    public String address;
}