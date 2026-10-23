package com.ntd.webcalculator.calculator;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class StringGenerationService {

	final String STRING_GENERATION_URL = "https://www.random.org/strings/?num=1&len=20&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new";

	private final RestClient restClient;

	public StringGenerationService(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}

	String generateString(){
		return this.restClient.get()
				.uri(STRING_GENERATION_URL)
				.retrieve()
				.body(String.class);
	}

}
