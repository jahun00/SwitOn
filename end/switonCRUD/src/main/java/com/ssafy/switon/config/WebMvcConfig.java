package com.ssafy.switon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ssafy.switon.Interceptors.CORSInterceptor;
import com.ssafy.switon.Interceptors.GetRequestInterceptor;
import com.ssafy.switon.Interceptors.OtherRequestInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

//	@Autowired
//	AuthInterceptor authInterceptor;
	@Autowired
	CORSInterceptor corsInterceptor;
	@Autowired
	GetRequestInterceptor getInterceptor;
	@Autowired
	OtherRequestInterceptor otherInterceptor;

	
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**")
////		registry.addMapping("/websocket/**")
//		.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
//	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload/**")
			.addResourceLocations("static/upload/");
	}



	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(corsInterceptor)
		.addPathPatterns("/**");
		
		registry.addInterceptor(otherInterceptor)
		.addPathPatterns("/study/**");
		
		registry.addInterceptor(getInterceptor)
		.addPathPatterns("/study/**/qna/**")
		.addPathPatterns("/study/**/repository/**")
		.addPathPatterns("/feeds")
		.addPathPatterns("/study/**/dashboard")
		.addPathPatterns("/study/**/schedule/**");
		
//		registry.addInterceptor(authInterceptor)
//		.addPathPatterns("/accounts/info")
//		.addPathPatterns("/study/create")
//		.addPathPatterns("/study/**/delete")
//		.addPathPatterns("/**/myqna")
//		.addPathPatterns("/**/myrepository");
	}
}
