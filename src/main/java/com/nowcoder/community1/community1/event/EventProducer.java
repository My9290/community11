package com.nowcoder.community1.community1.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community1.community1.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    //面向事件编程
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event){
        //将事件发布到指定指定主题,事件的内容是以json字符串的形式展现
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
