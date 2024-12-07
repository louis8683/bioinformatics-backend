package com.louislu.bioinformatics.data;

import org.springframework.boot.SpringApplication;

public class TestDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(DataServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
