package com.parserlabs.commons.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Component;

@Component
public class JsonReader {
	
	public String jsonReader(String filename) throws IOException {
		InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		StringBuilder stateJsonBuilder = new StringBuilder();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(resourceAsStream));
		String line;
		while ((line = bufferReader.readLine()) != null) {
			stateJsonBuilder.append(line + System.lineSeparator());
		}
		return stateJsonBuilder.toString();
	}

}
