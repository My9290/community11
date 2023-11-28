package com.nowcoder.community1.community1;

import com.nowcoder.community1.community1.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Community1Application.class)
public class SensitiveTest {
    //测试filter
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive(){
        //创建一个字符串
        String text = "本站不允许☆赌☆博、嫖娼、吸毒！";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
