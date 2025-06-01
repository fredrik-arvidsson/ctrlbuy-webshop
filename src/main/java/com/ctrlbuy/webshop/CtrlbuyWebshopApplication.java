package com.ctrlbuy.webshop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
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
		// KOMMENTERAR BORT DENNA PROBLEMATISKA RAD:
		// registry.addViewController("/products").setViewName("products-backup");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("\n=== SPRING BOOT DEBUG INFO ===");

			// Lista alla controllers
			System.out.println("=== REGISTERED CONTROLLERS ===");
			String[] beanNames = ctx.getBeanNamesForType(Object.class);
			for (String beanName : beanNames) {
				if (beanName.toLowerCase().contains("controller")) {
					Object bean = ctx.getBean(beanName);
					System.out.println("Controller: " + beanName + " -> " + bean.getClass().getName());
				}
			}

			// Lista alla URL-mappningar
			System.out.println("\n=== URL MAPPINGS ===");
			try {
				RequestMappingHandlerMapping mapping = ctx.getBean(RequestMappingHandlerMapping.class);
				mapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
					System.out.println("Mapping: " + requestMappingInfo + " -> " + handlerMethod);
				});
			} catch (Exception e) {
				System.out.println("Could not get URL mappings: " + e.getMessage());
			}

			System.out.println("=== END DEBUG INFO ===\n");
		};
	}
}