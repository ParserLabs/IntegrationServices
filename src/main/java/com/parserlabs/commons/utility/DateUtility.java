package com.parserlabs.commons.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class DateUtility {

	public LocalDateTime getDateTime(String dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		return LocalDateTime.parse(dateTime, formatter);
	}

	public LocalDateTime getCurretnDateTime() {
		Date date = new Date();
		Instant instant = Instant.ofEpochMilli(date.getTime());
		return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	}

	public LocalDate parseStringToLocalDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return LocalDate.parse(date, formatter);
	}
	
	
	public String dateConvertor(String dateStr) {

		String outputDate = dateStr;
		if (dateStr.length() != 4) {
			try {
				SimpleDateFormat inputFormatter = new SimpleDateFormat("dd-MM-yyyy");
				Date inputDate = inputFormatter.parse(dateStr);

				SimpleDateFormat outputformatter = new SimpleDateFormat("yyyy-MM-dd");
				outputDate = outputformatter.format(inputDate);
			} catch (ParseException e) {
				log.error("Parser exception occured during date conversion:", e);
			}
		}
		return outputDate;
	}


}
