package com.restapi.networkadmin.serviceI;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.restapi.networkadmin.requestVO.DeleteTransferProfileDataReqVO;
import com.restapi.networkadmin.requestVO.ModifyTransferProfileDataCloneReqVO;
import com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO;
import com.restapi.networkadmin.responseVO.DeleteTransferProfileRespVO;
import com.restapi.networkadmin.responseVO.ModifyTransferProfileRespVO;
import com.restapi.networkadmin.responseVO.SaveTransferProfileRespVO;
import com.restapi.superadmin.requestVO.FetchTransferProfilebyIDReqVO;
import com.restapi.superadmin.requestVO.TransferProfileLoadReqVO;
import com.restapi.superadmin.requestVO.TransferProfileSearchReqVO;
import com.restapi.superadmin.responseVO.FetchTransferProfileRespVO;
import com.restapi.superadmin.responseVO.TransferProfileLoadRespVO;
import com.restapi.superadmin.responseVO.TransferProfileSearchRespVO;

public interface TransfercontrolProfileServiceI {
	
	
	public TransferProfileSearchRespVO searchTransferProfileList(TransferProfileSearchReqVO request, String msisdn, HttpServletRequest httpServletRequest,
			HttpServletResponse responseSwag, Locale locale) throws BTSLBaseException;
	
	
	public TransferProfileLoadRespVO loadTransferProfilebyCat(TransferProfileLoadReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale) throws BTSLBaseException;
	
	public SaveTransferProfileRespVO saveTransferControlProfile(SaveTransferProfileDataCloneReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale) throws BTSLBaseException;
	
	
	public FetchTransferProfileRespVO fetchTransferProfileDetails(FetchTransferProfilebyIDReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale) throws BTSLBaseException;

	public ModifyTransferProfileRespVO modifyTransferControlProfile(ModifyTransferProfileDataCloneReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws BTSLBaseException;
	
	public DeleteTransferProfileRespVO deleteTransferControlProfile(DeleteTransferProfileDataReqVO request, String msisdn,
			HttpServletRequest httpServletRequest, HttpServletResponse responseSwag, Locale locale)
			throws BTSLBaseException;

}
