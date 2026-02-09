package com.mostafa.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching 
public class FoodDeliveryTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodDeliveryTrackerApplication.class, args);
	}

}
