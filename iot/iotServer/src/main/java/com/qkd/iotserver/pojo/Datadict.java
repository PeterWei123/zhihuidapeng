package com.qkd.iotserver.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tbl_datadict")
public class Datadict {
    @TableId(type = IdType.AUTO)
    public Integer id;
    public String mykey;
    public String value;

    public Datadict(String mykey, String value) {
        this.mykey = mykey;
        this.value = value;
    }
}