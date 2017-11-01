package com.dp.html5Learn;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 自定义mvc配置
 * Created by dongp on 2017/10/2.
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.dp.html5Learn")
public class CustomMvcConfigCommon extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);

        registry.addViewController("/").setViewName("index");
        registry.addViewController("/learn1").setViewName("Learn1");


    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/templates/assets/");
        registry.addResourceHandler("/bootstrap/**").addResourceLocations("classpath:/templates/assets/bootstrap/");
        registry.addResourceHandler("/jquery/**").addResourceLocations("classpath:/templates/assets/jquery/");
        registry.addResourceHandler("/picture/**").addResourceLocations("classpath:/templates/assets/picture/");
    }
}
