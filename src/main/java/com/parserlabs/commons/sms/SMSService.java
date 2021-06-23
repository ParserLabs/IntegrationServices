package com.parserlabs.commons.sms;

public interface SMSService {

	boolean send(String phoneNumber, String message, String login, String pw, String templeId);

	boolean sendSMS(String phoneNumber, String message);

	boolean sendOTP(String phoneNumber, String message);

	boolean sendAuthOtp(String phoneNumber, String otp);

	Boolean sendVerificationOtp(String phoneNumber, String otp);

	boolean sendBenefitIntegrationsNotification(String healthIdNumber, String phoneNumber, String benefitName);

	boolean forgotHidOtp(String phoneNumber, String otp);

	boolean sendHealthIdSuccessNotification(String userName, String healthIdNumber, String phoneNumber);

	boolean sendHealthIdDeactivationNotification(String name, String healthIdNumber, String phoneNumber);

	boolean sendHealthIdReactivicationNotification(String name, String healthIdNumber, String phoneNumber);

	/**
	 * @apiNote Send Mobile OTP to the user
	 * @param mobile number, smsType, signature, OTPtype, message contains 
	 * @return true
	 */
	boolean sendOTSMSPHealth(String mobileNumber, String msgType, String signature, String OTPType,
			String[] userMessageParam);

}
