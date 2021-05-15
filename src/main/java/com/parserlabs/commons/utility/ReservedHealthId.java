package com.parserlabs.commons.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReservedHealthId {

	public static final String HEALTH_ID_SUFFIX = "SUFFIX";
	private static final String NDHM_HEALTH_ID = System.getProperty(HEALTH_ID_SUFFIX, "@ndhm");
	private static final String RESERVED_HEALTH_ID_FILE_NAME = "reserved-health-id.txt";
	private static final List<String> RESERVED_HEALTH_ID_LIST = new ArrayList<>();

	public ReservedHealthId() {
		loadList();
	}

	public boolean exist(String healthId) {
		return getAll().contains(deSanetizeHealthId(healthId));
	}

	public List<String> getAll() {
		return RESERVED_HEALTH_ID_LIST;
	}

	private String deSanetizeHealthId(String healthId) {
		if (healthId.contains(NDHM_HEALTH_ID)) {
			healthId = healthId.replace(NDHM_HEALTH_ID, "");
		}
		return healthId.trim().toLowerCase();
	}

	public String loadData() {
		String content = null;
		try {
			InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(RESERVED_HEALTH_ID_FILE_NAME);
			StringBuilder contentBuilder = new StringBuilder();
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(resourceAsStream));
			String line;
			while ((line = bufferReader.readLine()) != null) {
				contentBuilder.append(line + System.lineSeparator());
			}
			content = contentBuilder.toString();
		} catch (Exception e) {
			log.error("Exception occured while reading file: {} Error Msg : ", RESERVED_HEALTH_ID_FILE_NAME,
					e.getMessage());
		}
		return content;
	}

	private void loadList() {
		String content = loadData();
		if (!StringUtils.hasLength(content)) {
			String[] healthIdArray = content.split("\n");
			for (String healthId : healthIdArray) {
				if (!StringUtils.hasLength(healthId)) {
					RESERVED_HEALTH_ID_LIST.add(healthId.trim().toLowerCase());
				}
			}
		}
		log.info("Reserved Health list data loaded found {} records.", RESERVED_HEALTH_ID_LIST.size());
	}
}
