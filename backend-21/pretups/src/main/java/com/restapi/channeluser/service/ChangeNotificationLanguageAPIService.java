package com.restapi.channeluser.service;

import java.util.HashMap;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BaseResponse;

@Service
public interface ChangeNotificationLanguageAPIService {

	/**
	 * 
	 * @param headers
	 * @param requestMap
	 * @param responseSwag
	 * @return 
	 */
	public NotificationLanguageResponseVO loadUsersDetails(MultiValueMap<String, String> headers, HashMap<String, String> requestMap, HttpServletResponse responseSwag);
	
	/**
	 * 
	 * @param headers
	 * @param requestMap
	 * @param responseSwag
	 * @return
	 */
	public NotificationLanguageResponseVO loadUserPhoneDetailsByMsisdn(MultiValueMap<String, String> headers, HashMap<String, String> requestMap, HttpServletResponse responseSwag);
	
	/**
	 * 
	 * @param headers
	 * @param requestVO
	 * @param responseSwag
	 * @return
	 */
	public BaseResponse changeNotificationLanguage(MultiValueMap<String, String> headers, NotificationLanguageRequestVO requestVO, HttpServletResponse responseSwag);
}
