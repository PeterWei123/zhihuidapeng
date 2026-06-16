package com.qkd.iotserver.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qkd.iotserver.util.NotNull;
import lombok.Data;

@Data
@TableName("tbl_product")
public class Product {
    @TableId(type = IdType.AUTO)
    public Integer id;//主键
    @NotNull
    public String product;//唯一键
    @NotNull
    public Integer cid;//外键
    @NotNull
    public Integer uid;//外键
    public String devip;
    public String clientid;
    public Data ctime;
    @TableField(exist = false)//1.查询视图2.视图中有的属性，原表中无
    public String city;
    @TableField(exist = false)
    public String user;
}