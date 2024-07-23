package com.restapi.networkadmin.interfacePrefix;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;

public interface InterfacePrefixServiceI {

	InterfaceNetworkMappingPrefixListResponseVO getInterfaceNetworkMappingPrefixList(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, InterfaceNetworkMappingPrefixListResponseVO response) throws BTSLBaseException;

	SaveInterfaceNetworkMappingPrefixResponseVO saveInterfaceNetworkMappingPrefix(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, SaveInterfaceNetworkMappingPrefixResponseVO response,
			SaveInterfaceNetworkMappingPrefixRequestVO saveInterfaceNetworkMappingPrefixRequestVO) throws BTSLBaseException, SQLException;

}
