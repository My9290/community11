package com.nowcoder.community1.community1;

import com.nowcoder.community1.community1.dao.DiscussPostMapper;
import com.nowcoder.community1.community1.dao.LoginTicketMapper;
import com.nowcoder.community1.community1.dao.MessageMapper;
import com.nowcoder.community1.community1.dao.UserMapper;
import com.nowcoder.community1.community1.entity.DiscussPost;
import com.nowcoder.community1.community1.entity.LoginTicket;
import com.nowcoder.community1.community1.entity.Message;
import com.nowcoder.community1.community1.entity.User;
import net.minidev.json.JSONUtil;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;


    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void updateUser(){
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://www.nowcoder.com/102/png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150,"hello");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost post:list){
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);

    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc",1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters(){
        //查询当前用户的会话列表，每个会话框只显示最新的一条私信
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for(Message m:list){
            System.out.println(m);
        }
        //查询当前用户的会话数量
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
        //查询某个会话所包含的私信列表
        list = messageMapper.selectLetters("111_112", 0, 10);
        for(Message m:list){
            System.out.println(m);
        }
        //查询某个会话所包含的私信数量
        count= messageMapper.selectLetterCount("111_112");
        System.out.println(count);
        //查询未读私信的数量
        count =  messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(count);
    }


}
