package com.qkd.iotserver.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data//在编译时帮我们写好setget方法
@TableName("tbl_user")
public class User {

    @TableId(type = IdType.AUTO)
    public Integer  id;
    public String user;
    public String username;
    public String password;
    public String status;
    public String state;
    public String photo;
    public String mobile;


}
