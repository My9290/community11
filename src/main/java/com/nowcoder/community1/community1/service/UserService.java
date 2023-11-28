package com.nowcoder.community1.community1.service;

import com.nowcoder.community1.community1.dao.LoginTicketMapper;
import com.nowcoder.community1.community1.dao.UserMapper;
import com.nowcoder.community1.community1.entity.LoginTicket;
import com.nowcoder.community1.community1.entity.User;
import com.nowcoder.community1.community1.util.CommunityUtil;
import com.nowcoder.community1.community1.util.MailClient;
import com.nowcoder.community1.community1.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.ACTIVITY_REQUIRED;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Constants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.nowcoder.community1.community1.util.CommunityConstant.*;

@Service
public class UserService implements Constants {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community1.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;


    public User findUserById(int id){
        //从cache中查询
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
//        return userMapper.selectById(id);
    }

    /**
     * 返回一个结果，包含多种情况，使用map记录
     */
    public Map<String,Object> register(User user){

        HashMap<String, Object> map = new HashMap<>();
        //空值处理
        if(user==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //在注册用户时，用户已经输入账号名和密码、邮箱信息了，
        // 所以在添加用户时，除了系统需要对用户的密码进行加密操作，用户名和邮箱不再需要进行set添加了
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
        }

        /**
         * 验证账号是否已存在,如果存在，u不为null，否则为null
         */
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账户已存在");
        }

        /**
         * 验证邮箱是否存在
         */
        u = userMapper.selectByName(user.getEmail());
        if(u!=null){
            map.put("emailMsg","该邮箱已被注册");
        }

        /**
         * 注册用户
         */
        //给原来的密码后面加上随机的字符串
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //原来的密码加上随机产生的字符串然后一起使用md5进行加密
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        //注册的用户都是普通用户
        user.setType(0);
        //注册还没有激活之前状态就是0，代表还没有注册成功，需要用激活码去验证
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nownode.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件，利用激活模版发邮件
        //设置模版里的变量
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/101/code要求的激活路径
        String url = domain+contextPath + "/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);

        //生成模版里面的内容
        String content = templateEngine.process("/mail/activation", context);
        //服务端发送邮件
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;

    }

    //返回激活的状态
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            //重复激活
            return ACTIVATION_REPEAT;
        } else if(user.getActivationCode().equals(code)){
            //激活成功，更新状态码为1
            userMapper.updateStatus(userId,1);
            //清理缓存
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            //激活失败
            return ACTIVATION_FAILURE;
        }
    }

    //返回登录结果
    public Map<String,Object> login(String username,String password,int expiredSecond){
        Map<String,Object> map = new HashMap<>();
        //处理参数是否为空
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","该账号不存在!");
            return map;
        }

        //判断账号是否激活
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活！");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSecond*1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    public void logout(String ticket){
//        loginTicketMapper.updateStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket)redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }
    //查询ticket对象
    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);

    }

    //更新用户图像路径
    public int updateHeader(int userId,String headerUrl){

//        return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;

    }

    //根据用户名获取用户信息
    public User findUserByName(String username){
        return userMapper.selectByName(username);

    }
    //1.优先从缓存中取值
    private User getCache(int userId){
       String redisKey = RedisKeyUtil.getUserKey(userId);
       //从redis取值
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2.如果取不到，就初始化缓存数据
    private User initCache(int userId){
        //从mysql中查到数据
        User user = userMapper.selectById(userId);
        String redisKey =  RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }

    //3.数据变更时清除缓存数据
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
        User user = this.findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
               switch (user.getType()){
                   case 1:
                       return AUTHORITY_ADMIN;
                   case 2:
                       return AUTHORITY_MODERATOR;
                   default:
                       return AUTHORITY_USER;
               }
            }
        });
        return list;


    }




}
