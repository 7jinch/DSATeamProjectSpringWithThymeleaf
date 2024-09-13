package com.example.tabitabi.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.tabitabi.filter.LogFilter;
import com.example.tabitabi.filter.LoginCheckFilter;
import com.example.tabitabi.interceptor.LogInterceptor;
import com.example.tabitabi.interceptor.LoginCheckInterceptor;

import jakarta.servlet.Filter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private String[] excludePaths = {"/","/*/join","/*/login", "/*/logout", "/*/forgotPw", "/*/setPw"
			 ,"/*.css", "/*.js", "/*.ico", "/error"};
	
    @Bean
    FilterRegistrationBean<Filter> logFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
    	//등록할 필터를 지정
		filterRegistrationBean.setFilter(new LogFilter());
		//필터의 순서적용. 숫자가 낮을 수록 먼저 실행.
		filterRegistrationBean.setOrder(1);
		//필터를 적용할 URL 패턴을 지정
		filterRegistrationBean.addUrlPatterns("/*"); //최상위경로 밑으로 들어오는 모든 경로에 필터를 적용하겠다
		
		return filterRegistrationBean;
	}
    
    @Bean
    FilterRegistrationBean<Filter> loginCheckFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
    	//등록할 필터를 지정
		filterRegistrationBean.setFilter(new LoginCheckFilter());
		//필터의 순서적용. 숫자가 낮을 수록 먼저 실행.
		filterRegistrationBean.setOrder(2);
		//필터를 적용할 URL 패턴을 지정
		filterRegistrationBean.addUrlPatterns("/*"); //최상위경로 밑으로 들어오는 모든 경로에 필터를 적용하겠다
		
		return filterRegistrationBean;
	}
	
    @Override
	public void addInterceptors(InterceptorRegistry registry) {
		//인터셉터 등록
    	registry.addInterceptor(new LogInterceptor())
    			.order(1) //인터셉터 호출 순서를 지정
    			.addPathPatterns("/**") //인터셉터를 적용할 URL 패턴 지정
    			.excludePathPatterns(excludePaths); //인터셉터에서 제외할 패턴을 지정
    	//인터셉터 등록
    	registry.addInterceptor(new LoginCheckInterceptor())
    	.order(2) //인터셉터 호출 순서를 지정
    	.addPathPatterns("/**") //인터셉터를 적용할 URL 패턴 지정
    	.excludePathPatterns(excludePaths); //인터셉터에서 제외할 패턴을 지정
    
	}
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
        		.addResourceLocations("classpath:/static/uploads/");
    }
}