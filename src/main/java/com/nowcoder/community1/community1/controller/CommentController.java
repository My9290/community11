package com.nowcoder.community1.community1.controller;

import com.nowcoder.community1.community1.entity.Comment;
import com.nowcoder.community1.community1.entity.DiscussPost;
import com.nowcoder.community1.community1.entity.Event;
import com.nowcoder.community1.community1.event.EventProducer;
import com.nowcoder.community1.community1.service.CommentService;
import com.nowcoder.community1.community1.service.DiscussServicePost;
import com.nowcoder.community1.community1.util.CommunityConstant;
import com.nowcoder.community1.community1.util.HostHolder;
import com.nowcoder.community1.community1.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussServicePost discussServicePost;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path="/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件
       Event event =  new Event()
               .setTopic(TOPIC_COMMENT)
               .setUserId(hostHolder.getUser().getId())
               .setEntityId(comment.getEntityId())
               .setEntityType(comment.getEntityType())
               .setData("postId",discussPostId);
       if(comment.getEntityType() == ENTITY_TYPE_POST){
           DiscussPost target = discussServicePost.findDiscussPostById(comment.getEntityId());
           event.setUserId(target.getUserId());
       }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
           Comment target = commentService.findCommentById(comment.getEntityId());
           event.setUserId(target.getUserId());
       }
       eventProducer.fireEvent(event);

       //只有评论给帖子才会有效，这里需要添加判断
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            //触发评论事件
            event =  new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
//                    .setUserId(hostHolder.getUser().getId())
                    .setEntityId(discussPostId)
                    .setEntityType(ENTITY_TYPE_POST);

            eventProducer.fireEvent(event);
            //计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,discussPostId);
        }




        return "redirect:/discuss/detail/"+discussPostId;
    }




}
