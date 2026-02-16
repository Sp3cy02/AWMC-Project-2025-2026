package com.gasing.hackhub;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class HackatonApplication {

	public static void main(String[] args) {
		
		new SpringApplicationBuilder().sources(HackatonApplication.class)
			//.child(figlio.class)
			//.bannerMode(Banner.Mode.OFF)
			.run(args);

		System.out.println("Hackaton Application is running...");	
	}

}
