package com.restapi.c2s.services;

import java.sql.Connection;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.AddServiceKeywordReq;
import com.btsl.pretups.channel.transfer.businesslogic.AddServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.DeleteServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetServiceKeywordListResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetServiceTypeListResp;
import com.btsl.pretups.channel.transfer.businesslogic.ModifyServiceKeywordReq;
import com.btsl.pretups.channel.transfer.businesslogic.ModifyServiceKeywordResp;
import com.btsl.pretups.channel.transfer.businesslogic.ServiceKeywordResp;
import com.btsl.user.businesslogic.UserVO;

@Service
public interface ServiceKeywordServiceI {
	
	public GetServiceTypeListResp getServiceTypeList(Connection con) throws BTSLBaseException ;
	public GetServiceKeywordListResp  searchServiceKeywordbyServiceType(Connection con, String inputServiceType) throws BTSLBaseException;
	public AddServiceKeywordResp addServiceKeyword(Connection con, AddServiceKeywordReq addServiceKeywordReq,UserVO userVO,Locale locale) throws BTSLBaseException;
	public ServiceKeywordResp  searchServiceKeywordbyID(Connection con, String servicekeywordID) throws BTSLBaseException;
	public ModifyServiceKeywordResp modifyServiceKeyword(Connection con,ModifyServiceKeywordReq modifyServiceKeywordReq ,UserVO userVO,Locale locale)
			throws BTSLBaseException;
	public DeleteServiceKeywordResp deleteServiceKeywordbyID(Connection con, String servicekeywordID,UserVO userVO) throws BTSLBaseException;
	


}
