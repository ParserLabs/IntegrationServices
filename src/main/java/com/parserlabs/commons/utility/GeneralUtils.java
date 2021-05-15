package com.parserlabs.commons.utility;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GeneralUtils {
	private static int offset;
	public static final String LATITUDE_PATTERN = "^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$";
	public static final String LONGITUDE_PATTERN = "^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$";
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	public static final Pattern MOBILE_REGEX = Pattern.compile("[6-9][0-9]{9}");
	private static final Pattern EMAIL_PATTERN = Pattern
			.compile("[a-zA-Z0-9!#$%&'*+/=?^_`{|}~.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*");
	public final Pattern SWASTHYA_ID_PATTERN = Pattern.compile("(\\d{4})(\\d{4})(\\d{4})(\\d{4})");
	public static final String HEALTH_ID_SUFFIX = "SUFFIX";
	private static String healthIdSuffix = System.getProperty(HEALTH_ID_SUFFIX, "@ndhm");

	public static String sanetizeHealthIdStr(String healthIdStr) {
		if (!healthIdStr.contains(healthIdSuffix)) {
			healthIdStr = healthIdStr + healthIdSuffix;
		}
		return healthIdStr.toLowerCase();
	}

	public static String formatDate(Date date) {
		return DATE_FORMAT.format(date);
	}

	public static String formatTime(Long timeInMillis) {
		return DATE_FORMAT.format(new Date(((long) timeInMillis) * 1000));
	}

	public static String currentTimeAsString() {
		return formatTime(getCurrentTime());
	}

	public static Long getCurrentTime() {
		return System.currentTimeMillis() + (offset * 1000);
	}

	public static LocalDate getCurrentDate() {
		return LocalDate.now();
	}

	public static LocalDate createDate(int day, int month, int year) {
		return LocalDate.of(year, month, day);
	}

	public static LocalDate createDate(String day, String month, String year) {
		return createDate(Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year));
	}

	/**
	 * Get DOB or YOB string based on data passed. YOB is always required.
	 * 
	 * @param day   day of birth (optional)
	 * @param month month of birth (optional)
	 * @param year
	 * @return String in format of "DD/MM/YYYY" or "YYYY"
	 */
	public static String getDobYobAsString(String day, String month, String year) {
		StringBuilder result = new StringBuilder();
		if (!isBlank(day) && !isBlank(month)) {
			result.append(day + "/" + month + "/");
		}
		result.append(year);
		return result.toString();
	}

	/**
	 * Format mobile number as per Indian numbers.
	 * 
	 * @param mobileNumber
	 * @return formatted mobile number
	 */
	public static boolean isValidMobileNumber(String mobileNumber) {
		return MOBILE_REGEX.matcher(mobileNumber).matches();
	}

	public static boolean isValidLocation(String latitude, String longitude) {
		DecimalFormat df = new DecimalFormat("#.######");
		df.setRoundingMode(RoundingMode.UP);
		boolean isValidLat = df.format(Double.parseDouble(latitude)).matches(LATITUDE_PATTERN);
		boolean isValidLong = df.format(Double.parseDouble(longitude)).matches(LONGITUDE_PATTERN);

		return (isValidLat && isValidLong);
	}

	/**
	 * Check if string is empty (null or lenght is 0)
	 * 
	 * @param s to check
	 * @return true if string is empty
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	/**
	 * Check if string is blank (null or lenght is 0 or contains only white
	 * characters)
	 * 
	 * @param s to check
	 * @return true if string is blank
	 */
	public static boolean isBlank(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static boolean isEmailValid(String email) {
		return EMAIL_PATTERN.matcher(email).matches();
	}

	public static String getRandomCode(long nrOfDigits) {
		if (nrOfDigits < 1) {
			throw new RuntimeException("Number of digits must be bigger than 0");
		}

		return RandomStringUtils.randomNumeric((int) nrOfDigits);
	}

	/**
	 * Get a random integer between a range (inclusive).
	 * 
	 * @param maximum
	 * @param minimum
	 * @return
	 */
	public static int getRandomInteger(int maximum, int minimum) {
		return ((int) (Math.random() * (maximum - minimum))) + minimum;
	}

	public static String getClientIP(HttpServletRequest request) {
		final String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader != null) {
			return xfHeader.split(",")[0];
		}
		return request.getRemoteAddr();
	}

	public static String stringTrimmer(String str) {
		return StringUtils.hasLength(str) ? str : str.trim();
	}
}
