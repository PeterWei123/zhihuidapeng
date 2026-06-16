package com.qkd.iotserver.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qkd.iotserver.util.NotNull;
import lombok.Data;

import java.util.Date;

@Data//在编译时帮我们写好setget方法
@TableName("tbl_industry")
public class Industry {

    @TableId(type = IdType.AUTO)
    public Integer  id;
    @NotNull
    public String industry;
    @NotNull
    public String title;
    @NotNull
    public String content;
    public String logo;
    public Date ctime;



}