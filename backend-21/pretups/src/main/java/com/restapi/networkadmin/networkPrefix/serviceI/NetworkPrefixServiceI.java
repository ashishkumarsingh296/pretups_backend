package com.restapi.networkadmin.networkPrefix.serviceI;

import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.restapi.networkadmin.networkPrefix.requestVO.SaveNetworkPrefixReqVO;
import com.restapi.networkadmin.networkPrefix.responseVO.NetworkPrefixRespVO;

public interface NetworkPrefixServiceI {
	
	public NetworkPrefixRespVO loadNetworkPrefixDetails(String networkCode,Locale locale) throws BTSLBaseException;
	public NetworkPrefixRespVO saveNetworkPrefixDetails(String loggedinUserID,SaveNetworkPrefixReqVO saveNetworkPrefixReqVO, Locale locale)
			throws BTSLBaseException;
	
	

}
