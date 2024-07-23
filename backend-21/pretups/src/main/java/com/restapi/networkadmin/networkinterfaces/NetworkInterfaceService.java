package com.restapi.networkadmin.networkinterfaces;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingVO;
import com.btsl.user.businesslogic.UserVO;


public interface NetworkInterfaceService {

	public NetworkInterfacesVO loadInterfaceNetworkMappingList(UserVO userVO , HttpServletRequest request, HttpServletResponse response, Locale locale);
	
	public BaseResponse deleteNetworkInterfaces(UserVO userVO , HttpServletRequest request, HttpServletResponse response, String networkInterfaceId, Locale locale);
	
	public BaseResponse addNetworkInterfaceInterfaces(UserVO userVO , HttpServletRequest request, HttpServletResponse response, NetworkInterfaceRequestVO requestVO, Locale locale);

	public BaseResponse modifyNetworkInterfaceInterfaces(UserVO userVO , HttpServletRequest request, HttpServletResponse response, NetworkInterfaceRequestVO requestVO, Locale locale);

}
