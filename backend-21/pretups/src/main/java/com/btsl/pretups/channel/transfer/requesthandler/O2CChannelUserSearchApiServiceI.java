package com.btsl.pretups.channel.transfer.requesthandler;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;

@Service
public interface O2CChannelUserSearchApiServiceI {

	public  SearchChannelUserVOResponseVO processRequest(String userName,String channelOwnerCategoryUserID,String geoDomainCode,String channelOwnerCategory,
			String channelDomainCode,String categoryCode,
			MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag)  throws BTSLBaseException;
	public  GeoDomainCatResponse processRequestGeoDomainCat(
			MultiValueMap<String, String> headers,
			HttpServletResponse responseSwag)  throws BTSLBaseException;

}
