package com.atguigu.gmalllogger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.constants.GmallConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@Controller  =  @Controller+方法上的@ResponseBody
@RestController
@Slf4j
public class LoggerController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("t1")
    public String test1() {
        System.out.println("************");
        return "success";
    }

    @RequestMapping("t2")
    public String test2(@RequestParam("name") String nn, @RequestParam("age") String age) {
        System.out.println(nn + ":" + age);
        return "success";
    }

    @RequestMapping("log")
    public String getLogger(@RequestParam("logString") String logString) {
        //添加时间戳    在日志服务器添加时间戳更准确更统一  因为前端手机的时间可以随意设置
        JSONObject jsonObject = JSON.parseObject(logString);
        jsonObject.put("ts", System.currentTimeMillis());

        //写入日志
        log.info(logString);

        //写入Kafka
        if ("startup".equals(jsonObject.getString("type"))) {
            //写入启动日志主题
            kafkaTemplate.send(GmallConstants.GMALL_TOPIC_START, jsonObject.toString());
        } else {
            //写入事件日志主题
            kafkaTemplate.send(GmallConstants.GMALL_TOPIC_EVENT, jsonObject.toString());
        }

        return "success";
    }
}
