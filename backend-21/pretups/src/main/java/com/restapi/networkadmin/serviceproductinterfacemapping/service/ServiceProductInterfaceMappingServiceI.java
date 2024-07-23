package com.restapi.networkadmin.serviceproductinterfacemapping.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.serviceproductinterfacemapping.requestVO.AddServiceInterfaceMappingRequestVO;
import com.restapi.networkadmin.serviceproductinterfacemapping.requestVO.DeleteServiceInterfaceMappingRequestVO;
import com.restapi.networkadmin.serviceproductinterfacemapping.requestVO.ModifyServiceInterfaceMappingRequestVO;
import com.restapi.networkadmin.serviceproductinterfacemapping.responseVO.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.MultiValueMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

public interface ServiceProductInterfaceMappingServiceI {
    ServiceTypesAndInterfaceListResponseVO getServiceTypesAndInterfaceList(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ServiceTypesAndInterfaceListResponseVO response) throws BTSLBaseException;

    ServiceInterfaceMappingForViewResponseVO getServiceInterfaceMappingForView(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ServiceInterfaceMappingForViewResponseVO response, String serviceType) throws BTSLBaseException;


    //add apis start
    ServiceInterfaceMappingForAddResponseVO getServiceInterfaceMappingForAdd(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ServiceInterfaceMappingForAddResponseVO response, String serviceType, String interfaceType) throws BTSLBaseException;

    AddServiceInterfaceMappingResponseVO addServiceInterfaceMapping(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, AddServiceInterfaceMappingResponseVO response, AddServiceInterfaceMappingRequestVO requestVO) throws BTSLBaseException, SQLException;

    //modify apis starts
    ServiceInterfaceMappingForModifyResponseVO getServiceInterfaceMappingForModify(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ServiceInterfaceMappingForModifyResponseVO response, String serviceType) throws BTSLBaseException;

    ModifyServiceInterfaceMappingResponseVO modifyServiceInterfaceMapping(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ModifyServiceInterfaceMappingResponseVO response, ModifyServiceInterfaceMappingRequestVO requestVO) throws BTSLBaseException, SQLException;

    BaseResponse deleteServiceInterfaceMapping(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response, DeleteServiceInterfaceMappingRequestVO requestVO) throws SQLException, BTSLBaseException;

}
