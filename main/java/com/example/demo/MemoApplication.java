package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// memo어플의 시작점, 이 어노테이션이 붙어 있으며 어플리케이션 실행시 가장 먼저 실행
@SpringBootApplication 
public class MemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemoApplication.class, args);
	}

}
