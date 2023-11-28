package com.nowcoder.community1.community1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Community1Application {
	@PostConstruct
	public void init(){
//		解决netty启动冲突的问题
//		System.setProperty("io.netty.availableProcessors","false");
		System.setProperty("es.set.netty.runtime.available.processors", "false");
//		System.setProperty("es.set.netty.runtime.available.processors","false");

	}




	public static void main(String[] args) {
		SpringApplication.run(Community1Application.class, args);
	}
}
