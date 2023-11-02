package com.nowcoder.community1.community1;

import com.nowcoder.community1.community1.util.MailClient;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Community1Application.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail2(){
        Context context = new Context();
        context.setVariable("username","sunny");

        //调用模版引擎，生成动态网页
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("1438458857@qq.com","HTML",content);




    }
    @Test
    public void testTextMail(){
        mailClient.sendMail("1438458857@qq.com","TEST","hello");
    }

}
