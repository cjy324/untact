package com.sbs.untact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//스프링이 시작되면 우선으로 읽어봐야되는 설정(config)이란 것을 알려주는 것
public class WebMvcConfig implements WebMvcConfigurer {
	// beforeActionInterceptor 인터셉터 불러오기
	@Autowired
	@Qualifier("beforeActionInterceptor")
	HandlerInterceptor beforeActionInterceptor;

	// 이 함수는 인터셉터를 적용하는 역할
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// beforeActionInterceptor 인터셉터가 모든 액션 실행전에 실행되도록 처리
		registry.addInterceptor(beforeActionInterceptor).addPathPatterns("/**").excludePathPatterns("/resource/**");
	}
}