package com.nowcoder.community1.community1;

import com.nowcoder.community1.community1.config.AlphaConfig;
import com.nowcoder.community1.community1.dao.AlphaDao;
import com.nowcoder.community1.community1.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
//@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Community1Application.class)
class Community1ApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Test
	public void test(){
		System.out.println(applicationContext);
		AlphaDao alphadao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphadao.select());
		alphadao  = applicationContext.getBean("aliphaHibernate",AlphaDao.class);
		System.out.println(alphadao.select());
	}


	@Test
	public void testBeanManagement(){
		AlphaService applicationContext = this.applicationContext.getBean(AlphaService.class);
		System.out.println(applicationContext);
		applicationContext = this.applicationContext.getBean(AlphaService.class);
		System.out.println(applicationContext);
	}


	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat
				= applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired
	@Qualifier("aliphaHibernate")
	private AlphaDao alphaDao;
	@Test
	public void getBeanTest(){
		System.out.println(alphaDao.select());
	}

	@Autowired
	private AlphaService alphaService;
	//表现层调业务层，业务层调持久层
	@Test
	public void getDaoTest(){
		System.out.println(alphaService.find());
	}


}
