package com.ng.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService
{

	private final Map<String, String> phoneOtpStore = new ConcurrentHashMap<>();
	private final Map<String, OtpData> emailOtpStore = new ConcurrentHashMap<>();

	// ---------- PHONE OTP ----------
	public String sendOtpToPhone(String phone)
	{
		phone = normalizePhone(phone);
		String otp = generateSixDigitOtp();
		if(!otp.isEmpty())
		{
			phoneOtpStore.put(phone, otp);
			System.out.println("OTP for phone " + phone + " is: " + otp);
		}
		else
		{
			System.out.println("otp is null");
		}
	return otp;
	
	}

	public boolean verifyPhoneOtp(String phone, String otp)
	{
		phone = normalizePhone(phone);
		String storedOtp = phoneOtpStore.get(phone);
		if (storedOtp != null && storedOtp.equals(otp))
		{
			phoneOtpStore.remove(phone); 
			return true;
		}
		return false;
	}

	private String normalizePhone(String phone)
	{
		if (phone.startsWith("+91"))
		{
			return phone.substring(3);
		}
		return phone;
	}

	// ---------- EMAIL OTP ----------
	public String generateEmailOtp(String email)
	{
		System.out.println("email:  " + email);
		String otp = generateSixDigitOtp();
		
		emailOtpStore.put(email, new OtpData(otp, LocalDateTime.now().plusMinutes(5)));
		System.out.println("emailOtpStore: "+emailOtpStore);
		return otp;
	}

	public boolean verifyEmailOtp(String email, String otp)
	{
		OtpData data = emailOtpStore.get(email);
	
		System.out.println("email: " + email);
		System.out.println("data: otp " + data + "|" + otp);
		
		if (data == null)
			return false;

		if (data.getExpiryTime().isBefore(LocalDateTime.now()))
		{
			emailOtpStore.remove(email); // expired
			return false;
		}

		if (data.getOtp().equals(otp))
		{
			emailOtpStore.remove(email); // one-time use
			return true;
		}
		return false;
	}

	// ---------- HELPER ----------
	private String generateSixDigitOtp()
	{
		return String.valueOf(100000 + new Random().nextInt(900000));
	}

	private static class OtpData
	{
		private final String otp;
		private final LocalDateTime expiryTime;

		public OtpData(String otp, LocalDateTime expiryTime)
		{
			this.otp = otp;
			this.expiryTime = expiryTime;
		}

		public String getOtp()
		{
			return otp;
		}

		public LocalDateTime getExpiryTime()
		{
			return expiryTime;
		}
	}
}
