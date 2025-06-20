package com.ctrlbuy.webshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Grundläggande sidor
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/home").setViewName("login");
        registry.addViewController("/welcome").setViewName("welcome");

        // Statiska sidor (endast sidor utan logik)
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/about").setViewName("about");
        registry.addViewController("/contact").setViewName("contact");
        registry.addViewController("/kontakt").setViewName("contact");
        registry.addViewController("/support").setViewName("support");

        // TA BORT DESSA RADER - de blockar din ProductController:
        // registry.addViewController("/products").setViewName("products-backup");
        // registry.addViewController("/produkter").setViewName("products-backup");
    }
}