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

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnExpression("${nic.sms.sender.service.enabled:false}")
public class SmsSender {
	
	private static final String GW_URL = "https://smsgw.sms.gov.in/failsafe/HttpLink?";
	
	public boolean send(String phoneNumber, String message, String login, String pw, String templeId, String signature, String entityId) {

		String postData = "username=" + login + "&pin=" + pw + "&message=" + message + "&mnumber=" + phoneNumber
				+ "&signature=" + signature + "&dlt_template_id=" + templeId + "&dlt_entity_id=" + entityId;
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

	public String sendSingleSMS(String smsURL, String postData, String mnumber) {
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
}
