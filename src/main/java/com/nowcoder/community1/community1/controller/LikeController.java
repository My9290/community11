package com.nowcoder.community1.community1.controller;


import com.nowcoder.community1.community1.entity.Event;
import com.nowcoder.community1.community1.entity.User;
import com.nowcoder.community1.community1.event.EventProducer;
import com.nowcoder.community1.community1.service.LikeService;
import com.nowcoder.community1.community1.util.CommunityConstant;
import com.nowcoder.community1.community1.util.CommunityUtil;
import com.nowcoder.community1.community1.util.HostHolder;
import com.nowcoder.community1.community1.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 异步请求，只更新页面点赞数，不用刷新页面
     */
    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user = hostHolder.getUser();

        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        //数量和状态需要打包一起传给页面
        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        //触发点赞事件
        if(likeStatus==1){
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("PostId",postId);
            eventProducer.fireEvent(event);

        }
        if(entityType==ENTITY_TYPE_POST){
            //计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,postId);
        }
        return CommunityUtil.getJSONString(0,null,map);


    }











}
