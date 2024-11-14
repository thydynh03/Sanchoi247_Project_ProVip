package com.example.SanChoi247;

// import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.PropertySource;
@EntityScan(basePackages = "com.example.SanChoi247.model.entity") // Thêm package chứa entity
@ComponentScan(basePackages = "com.example.SanChoi247")
@SpringBootApplication
// @PropertySource("classpath:secrets.properties")
public class SanChoi247Application {
	public static void main(String[] args) {
		// TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		SpringApplication.run(SanChoi247Application.class, args);
	}
}
