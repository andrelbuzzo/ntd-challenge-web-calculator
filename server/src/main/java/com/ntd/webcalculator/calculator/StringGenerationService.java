package com.ntd.webcalculator.calculator;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StringGenerationService {

	final String STRING_GENERATION_URL = "https://www.random.org/strings/?num=1&len=32&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new";

	private final RestTemplate restTemplate;

	public StringGenerationService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	String generateString(){
		return this.restTemplate.getForObject(STRING_GENERATION_URL, String.class);
	}

}
