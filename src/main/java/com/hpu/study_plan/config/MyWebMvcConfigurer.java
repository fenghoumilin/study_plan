package com.hpu.study_plan.config;

import com.hpu.study_plan.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    //拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> patternList = new ArrayList<>();
        patternList.add("/group/createUI");
        patternList.add("/group/create");
        patternList.add("/user/setting");
        patternList.add("/user/settingUI");


        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(patternList);
    }

    //图片映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("file:/Users/liuwei/graduation_project/study_plan/src/main/resources/static/img/");
    }
}
