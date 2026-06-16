package com.qkd.iotserver.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qkd.iotserver.util.NotNull;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tbl_device")
public class Device {
    @TableId(type = IdType.AUTO)
    public Integer id;
    @NotNull
    public String device;
    public String idcard;
    public void setIdcard(){
        this.idcard = this.device+"_"+this.ctime.getTime();
    }
    @NotNull
    public Integer cid;
    @NotNull
    public Integer pid;
    public Date ctime=new Date();

    @TableField(exist=false)
    public String category;
    @TableField(exist=false)
    public Integer pin;
    @TableField(exist=false)
    public String product;
    @TableField(exist=false)
    public Integer uid;
}