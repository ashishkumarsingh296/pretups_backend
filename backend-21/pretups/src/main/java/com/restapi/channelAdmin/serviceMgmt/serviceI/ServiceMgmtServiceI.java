package com.restapi.channelAdmin.serviceMgmt.serviceI;

import java.sql.SQLException;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.restapi.channelAdmin.serviceMgmt.requestVO.AddServiceMgmtReqVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.AddServiceMgmtRespVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.SearchServiceMgmtRespVO;
import com.restapi.channelAdmin.serviceMgmt.responseVO.ServiceMgmtInptRespVO;

public interface ServiceMgmtServiceI {
	
	public ServiceMgmtInptRespVO getServiceMgmtInputValues(String loginID,Locale locale) throws BTSLBaseException;
	public SearchServiceMgmtRespVO searchServiceMgmtData(String loginID,String serviceType,String domainCode ,Locale locale) throws BTSLBaseException;
	public AddServiceMgmtRespVO addserviceMgmtData(AddServiceMgmtReqVO addServiceMgmtReqVO,String loginID,Locale locale ) throws BTSLBaseException, SQLException;

}
