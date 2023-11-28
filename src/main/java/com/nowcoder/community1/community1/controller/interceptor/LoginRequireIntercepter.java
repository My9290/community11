package com.nowcoder.community1.community1.controller.interceptor;

import com.nowcoder.community1.community1.annotation.LoginRequired;
import com.nowcoder.community1.community1.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

@Component
public class LoginRequireIntercepter implements HandlerInterceptor {
    //在请求之初判断是否登录

    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            //判断该方法上有没有LoginRequired 注解，
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //如果有，表明当前方法在访问之前需要登录，但是当前线程中获取的用户状态为空，说明用户还没有进行登录，
            //则要进行拦截
            if(loginRequired!=null && hostHolder.getUser() == null){
                //拦截到了，请先登录（这里需要使用response返回到登录页面，而不能使用模板）
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
