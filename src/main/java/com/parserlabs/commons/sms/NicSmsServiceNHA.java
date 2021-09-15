package com.parserlabs.commons.sms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service("SMSServiceNHA")
@Slf4j
@ConditionalOnExpression("${nic.sms.service.enabled:false}")
public class NicSmsServiceNHA implements SMSServiceNHA {

	private static final String GW_URL = "https://smsgw.sms.gov.in/failsafe/HttpLink?";

	@Value("${app.entityId.nha}")
	private String entityId;

	@Autowired
	private MessageSource messageSource;

	@Value("${smsservice.enabled:false}")
	private boolean otpEnabled;

	@Value("${sms.service.username}")
	private String smsServiceUsername;

	@Value("${otp.service.username}")
	private String otpServiceUsername;

	@Value("${sms.service.pwd}")
	private String smsServicePwd;

	@Value("${otp.service.pwd}")
	private String otpServicePwd;

	@Value("${app.home}")
	private String applicationUrl;

	@Value("${smsservice.signature.nha:NHASMS}")
	private String signature;

	@Autowired
	HttpServletRequest httpRequest;

	@Override
	public boolean forgotHidOtp(String phoneNumber, String otp) {
		String[] messageParams = new String[1];
		messageParams[0] = otp;
		log.info("Sending hid recovery msg to : {}", phoneNumber);
		return sendOTP(phoneNumber,
				messageSource.getMessage("forgot.hid.otp.msg", messageParams, httpRequest.getLocale()));
	}

	@Override
	public boolean sendAuthOtp(String phoneNumber, String otp) {
		String[] messageParams = new String[1];
		messageParams[0] = otp;
		log.info("Sending auth msg to : {}", phoneNumber);
		return sendOTP(phoneNumber,
				messageSource.getMessage("authentication.otp.msg", messageParams, httpRequest.getLocale()));
	}

	@Override
	public boolean sendBenefitIntegrationsNotification(String healthIdNumber, String phoneNumber, String benefitName) {
		String[] messageParams = new String[2];
		messageParams[0] = benefitName;
		messageParams[1] = applicationUrl;
		log.info("Sending hid benefit : {} integration msg to : {} on {}", healthIdNumber, benefitName, phoneNumber);
		return sendSMS(phoneNumber,
				messageSource.getMessage("hid.notifybenefit.msg", messageParams, httpRequest.getLocale()));
	}

	@Override
	public boolean sendHealthIdSuccessNotification(String userName, String healthIdNumber, String phoneNumber) {
		String[] messageParams = new String[3];
		messageParams[0] = userName;
		messageParams[1] = healthIdNumber;
		messageParams[2] = applicationUrl;
		log.info("Sending success notification msg to : {}", phoneNumber);
		return sendSMS(phoneNumber,
				messageSource.getMessage("registration.success.otp", messageParams, httpRequest.getLocale()));
	}

	@Async
	public boolean sendOTP(String phoneNumber, String message) {
		boolean result = true;
		if (otpEnabled) {
			send(phoneNumber, message(message), otpServiceUsername, otpServicePwd, templeId(message));
		}
		return result;
	}

	@Async
	public boolean sendSMS(String phoneNumber, String message) {
		boolean result = true;
		if (otpEnabled) {
			send(phoneNumber, message(message), smsServiceUsername, smsServicePwd, templeId(message));
		} else {
			log.info("SMS Service disabled.");
		}
		return result;
	}

	@Override
	public Boolean sendVerificationOtp(String phoneNumber, String otp) {
		String[] messageParams = new String[1];
		messageParams[0] = otp;
		log.info("Sending verification msg to {}", phoneNumber);
		return sendOTP(phoneNumber,
				messageSource.getMessage("registration.otp.msg", messageParams, httpRequest.getLocale()));
	}

	private String message(String message) {
		return !StringUtils.isEmpty(message) ? StringUtils.substringBefore(message, "##") : null;
	}

	private String templeId(String message) {
		return !StringUtils.isEmpty(message) ? StringUtils.substringAfter(message, "##") : null;
	}

	@Override
	public boolean sendHealthIdDeactivationNotification(String name, String healthIdNumber, String phoneNumber) {
		String[] messageParams = new String[3];
		messageParams[0] = name;
		messageParams[1] = healthIdNumber;
		messageParams[2] = applicationUrl;
		log.info("Sending account deactivation notification msg to : {}", phoneNumber);
		return sendSMS(phoneNumber,
				messageSource.getMessage("hid.account.deactivate.msg", messageParams, httpRequest.getLocale()));
	}

	@Override
	public boolean sendHealthIdReactivicationNotification(String name, String healthIdNumber, String phoneNumber) {
		String[] messageParams = new String[3];
		messageParams[0] = name;
		messageParams[1] = healthIdNumber;
		log.info("Sending account reactivation notification msg to : {}", phoneNumber);
		return sendSMS(phoneNumber,
				messageSource.getMessage("hid.account.reactivate.msg", messageParams, httpRequest.getLocale()));
	}

	private boolean send(String phoneNumber, String message, String login, String pw, String templeId) {

		String postData = "username=" + login + "&pin=" + pw + "&message=" + message + "&mnumber=" + phoneNumber
				+ "&signature=" + signature + "&dlt_template_id=" + templeId + "&dlt_entity_id=" + entityId;
		// log.info("Postdata: {}", postData);
		try {
			String response = sendSingleSMS(GW_URL, postData, phoneNumber);
			log.info("SMS Sent: To mobile Number: {} TemplateId:{} GWResponse:{}", phoneNumber, templeId, response);
		} catch (Exception ex) {
			log.error("SMS Error: To mobile Number: {} TemplateId:{} GWResponse:{}", phoneNumber, templeId,
					ex.toString());
			return false;
		}
		return true;
	}

	private String sendSingleSMS(String smsURL, String postData, String mnumber) {
		InputStream is = null;
		OutputStream os = null;
		int responseCode = 0;
		StringBuilder responseBuffer = new StringBuilder();
		String responseMsg = "";
		try {
			URL url = new URL(smsURL);
			URLConnection conn = url.openConnection();

			if (conn instanceof HttpsURLConnection) {
				log.debug("HTTPS");
				HttpsURLConnection con = (HttpsURLConnection) conn;
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setConnectTimeout(20000);
				con.setReadTimeout(20000);
				con.connect();
				try {
					os = con.getOutputStream();
				} catch (Exception e) {
					os = null;
					log.error("## Exception while fetching output stream of the connection. URL --> " + url);
				}
				if (os != null)
					os.write(postData.getBytes(StandardCharsets.UTF_8));

				responseCode = con.getResponseCode();
				try {
					is = con.getInputStream();
				} catch (Exception e) {
					is = null;
					log.error("## Exception while fetching input stream of the connection. URL --> " + url);
				}
			} else {
				log.debug("HTTP");
				HttpURLConnection con = (HttpURLConnection) conn;
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setConnectTimeout(20000);
				con.setReadTimeout(20000);
				con.connect();
				try {
					os = con.getOutputStream();
				} catch (Exception e) {
					os = null;
					responseBuffer.append("Exception while sending SMS " + e.getMessage());
				}
				if (os != null)
					os.write(postData.getBytes(StandardCharsets.UTF_8));

				responseCode = con.getResponseCode();
				try {
					is = con.getInputStream();
				} catch (Exception e) {
					is = null;
					responseBuffer.append("Exception while sending SMS " + e.getMessage());
				}
			}
			if (responseCode == HttpsURLConnection.HTTP_OK && is != null) {
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
				String inputLine = "";

				while ((inputLine = responseReader.readLine()) != null) {
					responseBuffer.append(inputLine);
				}

				responseReader.close();
				is.close();
				responseMsg = responseBuffer.toString();
				if (responseMsg.contains("Message Rejected")) {
					throw new Exception(responseMsg);
				}
			}
		} catch (Exception e) {
			responseBuffer.append("Exception while sending SMS " + e.getMessage());
			log.error("Exception occured while sending SMS:", e);
		}

		return responseBuffer.toString();
	}

	@Override
	public boolean sendDocumentIdSuccessNotification(String userName, String healthIdNumber, String phoneNumber) {
		String[] messageParams = new String[2];
		messageParams[0] = userName;
		messageParams[1] = healthIdNumber;
		log.info("Sending success notification msg to : {}", phoneNumber);
		String message = messageSource.getMessage("registration.success.otp", messageParams, httpRequest.getLocale());
		if(message.contains("{1}")) {
			message = message.replace("{1}", healthIdNumber);
		}
		return sendSMS(phoneNumber, message);
	}

}
