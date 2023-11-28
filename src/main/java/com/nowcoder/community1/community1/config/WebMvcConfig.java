package com.nowcoder.community1.community1.config;

import com.nowcoder.community1.community1.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *   配置拦截器，为它指定拦截的路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AlphaIntercepter alphaIntercepter;
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
//    @Autowired
//    private LoginRequireIntercepter loginRequireIntercepter;
    @Autowired
    private MessageIntercepter messageIntercepter;
    @Autowired
    private DataIntercepter dataIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器并设置拦截条件/**代表所有目录，/**/*.css排除掉所有目录下的css文件,添加拦击的页面
        registry.addInterceptor(alphaIntercepter)
                .excludePathPatterns("/**/*.css","/**.*.js","/**/*.png","/**/*.jpeg")
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**.*.js","/**/*.png","/**/*.jpeg");

//        registry.addInterceptor(loginRequireIntercepter)
//                .excludePathPatterns("/**/*.css","/**.*.js","/**/*.png","/**/*.jpeg");
        registry.addInterceptor(messageIntercepter)
                .excludePathPatterns("/**/*.css","/**.*.js","/**/*.png","/**/*.jpeg");

        registry.addInterceptor(dataIntercepter)
                .excludePathPatterns("/**/*.css","/**.*.js","/**/*.png","/**/*.jpeg");
    }
}
