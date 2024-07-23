package com.restapi.networkadmin.servicePrdMapping.service;

import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.pretups.master.businesslogic.SelectorAmountMappingVO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.servicePrdMapping.responseVO.SearchServicePrdMappingRespVO;
import com.restapi.networkadmin.servicePrdMapping.responseVO.ServicePrdInputRespVO;

public interface ServicePrdMappingIntf {
	public ServicePrdInputRespVO getServicePrdMappingUIInputValues(UserVO userVO, Locale locale) throws BTSLBaseException;
	public SearchServicePrdMappingRespVO  searchServicePrdMapping(String serviceType,String selectorCode,UserVO userVO, Locale locale) throws BTSLBaseException;
	public BaseResponse  saveServicePrdMapping(SelectorAmountMappingVO selectorAmountMappingVO,UserVO userVO, Locale locale) throws BTSLBaseException;
	public BaseResponse modifyServicePrdMapping(SelectorAmountMappingVO selectorAmountMappingVO, UserVO userVO,
			Locale locale) throws BTSLBaseException;
	public BaseResponse deleteServicePrdMapping(SelectorAmountMappingVO selectorAmountMappingVO, UserVO userVO,
			Locale locale) throws BTSLBaseException;
}
