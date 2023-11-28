package com.nowcoder.community1.community1.controller;

import com.nowcoder.community1.community1.entity.DiscussPost;
import com.nowcoder.community1.community1.entity.Page;
import com.nowcoder.community1.community1.entity.User;
import com.nowcoder.community1.community1.service.DiscussServicePost;
import com.nowcoder.community1.community1.service.LikeService;
import com.nowcoder.community1.community1.service.UserService;
import com.nowcoder.community1.community1.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussServicePost discussServicePost;

    @Autowired
    private UserService userServive;

    @Autowired
    private LikeService likeService;

    /**
     * 因为返回的是html页面，所以不用添加reponsebody注解
     */
    @RequestMapping(path="index",method = RequestMethod.GET)
    /**
     * 在方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model,
     * 所以，在thymeleaf中可以直接访问Page对象中的数据
     */
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        page.setRows(discussServicePost.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);
        List<DiscussPost> list = discussServicePost.findDiscussPosts(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<Map<String, Object>>();
        if(list!=null){
            for(DiscussPost post:list){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("post",post);
                User user = userServive.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "/index";
        //更新代码提交到github
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "error/500";
    }
    @RequestMapping(path = "/denied",method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";

    }}
