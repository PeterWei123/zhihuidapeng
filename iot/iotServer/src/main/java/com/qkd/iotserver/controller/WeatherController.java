package com.qkd.iotserver.controller;

import com.qkd.iotserver.pojo.Record;
import com.qkd.iotserver.util.HttpConn;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/weather")
public class WeatherController {

    // @RequestParam接收code参数（默认值设为青岛的54857）
    @RequestMapping("/today")
    public Record today(@RequestParam(defaultValue = "54857") String code) {
        Record record=new Record();
        try{
            HttpConn.mMapHeader=new HashMap<>();
            // 将Cookie中的lastCountyId替换为变量code
            HttpConn.mMapHeader.put("Cookie","lc2=53798; lc=53798; wc=53798; wc_n=%25u4FE1%25u90FD%25u533A; __ad_cookie_mapping_tck_630=e549ec7a7c29e6396f6902a83de4a73b; Hm_lvt_a3f2879f6b3620a363bec646b7a8bcdd=1765068305,1765077216,1765153139,1765677144; HMACCOUNT=007CB2680F9187E8; BAIDU_SSP_lcr=https://www.baidu.com/link?url=UusiEH7FRJvYSMFWhsvTWNm4yDiFJqurxkbLMwjmhgVuCxTynH5h_SwrhUk_hL0e&wd=&eqid=f750be9e000107cb00000003693e1855; lastTownId=-1; lastProvinceId=31; lastCountyId="+code+"; lastTownTime=1765681586; Hm_lpvt_a3f2879f6b3620a363bec646b7a8bcdd=1765681604; lastCountyTime=1765681604;");
            String html=HttpConn.get("https://tianqi.2345.com/");
            Document dom=Jsoup.parse(html);


            List<Integer>highest=new ArrayList<>();
            List<Integer>lowest=new ArrayList<>();
            for(Element e:dom.getElementsByClass("banner-right-con-list-temp")){
                String temp=e.text().replace("°","");
                lowest.add(Integer.parseInt(temp.split("~")[0]));
                highest.add(Integer.parseInt(temp.split("~")[1]));
            }
            List<String>timeList=new ArrayList<>();
            for(Element e:dom.getElementsByClass("banner-right-con-list-time")){
                timeList.add(e.text());
            }
            List<String>statusList=new ArrayList<>();
            for(Element e:dom.getElementsByClass("banner-right-con-list-status")){
                statusList.add(e.text());
            }
            record.put("timeList",timeList);
            record.put("statusList",statusList);
            record.put("highest",highest);
            record.put("lowest",lowest);
        }catch(Exception e){
            System.out.println("1.断网了 2.网址写错了 3对方网站不让爬");
        }
        return record;
    }
}