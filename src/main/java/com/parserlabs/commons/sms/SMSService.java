package com.parserlabs.commons.sms;

public interface SMSService {
	
	boolean forgotHidOtp(String phoneNumber, String otp);

	boolean send(String phoneNumber, String message, String login, String pw, String templeId);

	boolean sendAuthOtp(String phoneNumber, String otp);

	boolean sendBenefitIntegrationsNotification(String healthIdNumber, String phoneNumber, String benefitName, String applicationUrl);

	boolean sendHealthIdSuccessNotification(String userName, String healthIdNumber, String phoneNumber, String applicationUrl);

	boolean sendOTP(String phoneNumber, String message);

	boolean sendSMS(String phoneNumber, String message);

	Boolean sendVerificationOtp(String phoneNumber, String otp);
}
