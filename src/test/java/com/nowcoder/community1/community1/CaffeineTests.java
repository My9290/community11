package com.nowcoder.community1.community1;

import com.nowcoder.community1.community1.entity.DiscussPost;
import com.nowcoder.community1.community1.service.DiscussServicePost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Community1Application.class)
public class CaffeineTests {
    @Autowired
    private DiscussServicePost postService;

    @Test
    public void initDataForTest(){
        for(int i = 0;i<300000;i++){
            DiscussPost post = new DiscussPost();
            post.setUserId(11);
            post.setTitle("互联网求职暖春计划");
            post.setContent("心态放好，坚持就会胜利！");
            post.setCreateTime(new Date());
            post.setScore(Math.random()*2000);
            postService.addDiscussPost(post);
        }
    }
    @Test
    public void testCache(){
        System.out.println(postService.findDiscussPosts(0,0,10,1));
        System.out.println(postService.findDiscussPosts(0,0,10,1));
        System.out.println(postService.findDiscussPosts(0,0,10,1));
        System.out.println(postService.findDiscussPosts(0,0,10,0));
    }



}
