package com.parserlabs.commons.sms;

public interface SMSServiceNHA {

	boolean sendAuthOtp(String phoneNumber, String otp);

	Boolean sendVerificationOtp(String phoneNumber, String otp);

	boolean sendBenefitIntegrationsNotification(String healthIdNumber, String phoneNumber, String benefitName);

	boolean forgotHidOtp(String phoneNumber, String otp);

	boolean sendHealthIdSuccessNotification(String userName, String healthIdNumber, String phoneNumber);
	
	boolean sendDocumentIdSuccessNotification(String userName, String healthIdNumber, String phoneNumber);

	boolean sendHealthIdDeactivationNotification(String name, String healthIdNumber, String phoneNumber);

	boolean sendHealthIdReactivicationNotification(String name, String healthIdNumber, String phoneNumber);
}
