package com.nowcoder.community1.community1.controller;

import com.nowcoder.community1.community1.entity.DiscussPost;
import com.nowcoder.community1.community1.entity.Page;
import com.nowcoder.community1.community1.entity.User;
import com.nowcoder.community1.community1.service.DiscussServicePost;
import com.nowcoder.community1.community1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussServicePost discussServicePost;

    @Autowired
    private UserService userServive;

    /**
     * 因为返回的是html页面，所以不用不用添加reponsebody注解
     */
    @RequestMapping(path="index",method = RequestMethod.GET)
    /**
     * 在方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model,
     * 所以，在thymeleaf中可以直接访问Page对象中的数据
     */
    public String getIndexPage(Model model, Page page){
        page.setRows(discussServicePost.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussServicePost.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<Map<String, Object>>();
        if(list!=null){
            for(DiscussPost post:list){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("post",post);

                User user = userServive.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }





}
