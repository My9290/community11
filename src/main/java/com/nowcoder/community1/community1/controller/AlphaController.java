package com.nowcoder.community1.community1.controller;

import com.nowcoder.community1.community1.Community1Application;
import com.nowcoder.community1.community1.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "hello Springboot";
    }


    @RequestMapping("/date")
    @ResponseBody
    public String requestProcess() {
        return alphaService.find();
    }


    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

        //获取头结点信息
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + "-->" + value);
        }
        System.out.println(request.getParameter("code"));
        //返回响应数据
        //设置返回响应数据格式
        response.setContentType("text/html;charset=utf-8");
        try (PrintWriter writer = response.getWriter();
        ) {
            //获取输出信息
            writer.write("<h1>牛客网<h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get请求
    //用于网页请求时填写的参数设置

    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            /**
             * 加入以下注解用于网页请求地址栏可以输入的参数，可以在控制台获取浏览器设置的参数
             */
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {

        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    /**
     * 每次都要先运行控制台程序，然后在浏览器输入地址栏参数，控制台就会输出结果
     */
    public String student(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //Post请求
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);

        return "success";
    }

    //响应HTML数据
    //方式一
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");
        return mav;
    }

    //方式二
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",40);
        return "demo/view";
    }

    //响应JSON数据（异步请求）
    //Java对象 -> JSON字符串->JS对象

    /**
     * 返回一个JSON对象
     */
    @RequestMapping(path = "/map",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",21);
        map.put("salary",30000);
        return map;
    }


    @RequestMapping(path = "/list",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getList(){
        List<Map<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",21);
        map.put("salary",30000);
        list.add(map);

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("name","小明");
        map2.put("age",32);
        map2.put("salary",20000);
        list.add(map2);
        return list;
    }






}











