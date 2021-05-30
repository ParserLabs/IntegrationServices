package com.parserlabs.commons.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnExpression("${allowed.location.enabled:false}")
public class AllowedLocations {

	private static final String ALLOWED_LOCATIONS_FILE_NAME = "allowed-locations.txt";
	private static final List<String> ALLOWED_LOCATIONS_LIST = new ArrayList<>();

	public AllowedLocations() {
		loadList();
	}

	public boolean exist(String location) {
		return getAll().contains(location.trim().toLowerCase());
	}

	public List<String> getAll() {
		return ALLOWED_LOCATIONS_LIST;
	}

	private String loadData() {
		String content = null;
		try {
			InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(ALLOWED_LOCATIONS_FILE_NAME);
			StringBuilder contentBuilder = new StringBuilder();
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(resourceAsStream));
			String line;
			while ((line = bufferReader.readLine()) != null) {
				contentBuilder.append(line + System.lineSeparator());
			}
			content = contentBuilder.toString();
		} catch (Exception e) {
			log.error("Exception occured while reading file: {} Error Msg : ", ALLOWED_LOCATIONS_FILE_NAME,
					e.getMessage());
		}
		return content;
	}

	private void loadList() {
		String content = loadData();
		if (!StringUtils.isEmpty(content)) {
			String[] healthIdArray = content.split("\n");
			for (String healthId : healthIdArray) {
				if (!StringUtils.isEmpty(healthId)) {
					ALLOWED_LOCATIONS_LIST.add(healthId.trim().toLowerCase());
				}
			}
		}
		log.info("Reserved Health list data loaded found {} records.", ALLOWED_LOCATIONS_LIST.size());
	}
}
