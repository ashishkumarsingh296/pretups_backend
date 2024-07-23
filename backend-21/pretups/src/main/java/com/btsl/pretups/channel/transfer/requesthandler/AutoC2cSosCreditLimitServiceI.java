package com.btsl.pretups.channel.transfer.requesthandler;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;


@Service
public interface AutoC2cSosCreditLimitServiceI {
	
	public void loadAutoC2cSosCreditLimitDetails(MultiValueMap<String, String> headers, HttpServletResponse responseSwag, AutoC2CSOSViewResponseVO responseVO);
	

	public AutoC2CSOSUpdateResponseVO processIndividualRecord(MultiValueMap<String, String> headers, HttpServletResponse responseSwag,
			AutoC2CSOSRequestVO requestVO);

}
