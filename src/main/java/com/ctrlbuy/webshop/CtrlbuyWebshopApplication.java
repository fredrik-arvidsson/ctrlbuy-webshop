package com.ctrlbuy.webshop;

import com.ctrlbuy.webshop.security.JwtService;
import com.ctrlbuy.webshop.security.util.SecurityUtils;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.ctrlbuy.webshop",
		"com.ctrlbuy.webshop.repository",
		"com.ctrlbuy.webshop.security",
		"com.ctrlbuy.webshop.model",
		"com.ctrlbuy.webshop.controller",
		"com.ctrlbuy.webshop.service"
})
public class CtrlbuyWebshopApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(CtrlbuyWebshopApplication.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("welcome");
		registry.addViewController("/welcome").setViewName("welcome");
		registry.addViewController("/home").setViewName("home");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/register").setViewName("register");
		registry.addViewController("/about").setViewName("about");
		registry.addViewController("/contact").setViewName("contact");
		registry.addViewController("/products").setViewName("products");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
	}

}
