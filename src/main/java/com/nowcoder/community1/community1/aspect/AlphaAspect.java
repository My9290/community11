package com.nowcoder.community1.community1.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.nio.channels.Pipe;

//@Component
//@Aspect
public class AlphaAspect {
    //方面组件要定义2个内容：
    /**
     * 1.定义切点：针对项目中的bean，要把代码织入到具体哪些Bean的哪些位置上
     * 2.
     */
    //该注解表示所有的service组件的所有方法所有的参数
    @Pointcut("execution(* com.nowcoder.community1.community1.service.*.*(..))")
    public void pointcut(){
    }
    /**
     * 通知分5类：
     * ①在连接点的开始做什么事
     * ②在连接点结束时做。。。
     * ③返回数据以后做。。。
     * ④抛异常做。。。
     * ⑤连接点前后同时做。。。
     */
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }
    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }
    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint)throws Throwable{
        System.out.println("之前做。。。");
        Object obj =  joinPoint.proceed();//调用目标组件要处理的方法
        System.out.println("之后做。。。");
        return obj;
    }


}
