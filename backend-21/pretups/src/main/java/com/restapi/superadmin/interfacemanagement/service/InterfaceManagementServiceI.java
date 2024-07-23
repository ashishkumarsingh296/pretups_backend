package com.restapi.superadmin.interfacemanagement.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.restapi.superadmin.interfacemanagement.requestVO.InterfaceDetailRequestVO;
import com.restapi.superadmin.interfacemanagement.requestVO.ModifyInterfaceDetailRequestVO;
import com.restapi.superadmin.interfacemanagement.responseVO.InterfaceDetailResponseVO;
import com.restapi.superadmin.interfacemanagement.responseVO.InterfaceTypeResponseVO;
import com.restapi.superadmin.interfacemanagement.responseVO.ModifyInterfaceDetailResponseVO;
import org.springframework.util.MultiValueMap;

import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public interface InterfaceManagementServiceI {
    InterfaceDetailResponseVO getInterfaceDetails(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, String loginID, Locale locale, String interfaceCategory) throws Exception, BTSLBaseException;

    InterfaceDetailResponseVO addInterfaceDetails(MultiValueMap<String, String> headers, Connection con, String loginID, Locale locale, InterfaceDetailRequestVO request, String interfaceCategory) throws Exception;

    ModifyInterfaceDetailResponseVO getInterfaceDetailsByInterfaceId(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, String loginID, Locale locale, String interfaceId) throws BTSLBaseException, SQLException;

    InterfaceDetailResponseVO modifyInterfaceDetails(MultiValueMap<String, String> headers, Connection con, String loginID, Locale locale, ModifyInterfaceDetailRequestVO request) throws Exception;

    InterfaceDetailResponseVO deleteInterfaceDetails(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, String loginID, Locale locale, String interfaceId) throws Exception;

    InterfaceTypeResponseVO loadInterfaceType(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, String loginID, Locale locale, String interfaceCategory) throws Exception, BTSLBaseException;
}
