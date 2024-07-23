package com.restapi.channelAdmin.batchUserApprove.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.channelAdmin.batchUserApprove.requestVO.BulkUserProcessRequestVO;
import com.restapi.channelAdmin.batchUserApprove.responseVO.BulkUserApproveRejectResponseVO;
import com.restapi.channelAdmin.batchUserApprove.responseVO.BulkUserProcessResponseVO;
import com.restapi.channelAdmin.batchUserApprove.responseVO.LoadBatchListForApprovalResponseVO;
import com.restapi.user.service.FileDownloadResponseMulti;

public interface BatchUserApproveServiceI {

	LoadBatchListForApprovalResponseVO loadBatchListForApproval(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,ChannelUserVO channelUserVO,
			LoadBatchListForApprovalResponseVO response,String domainCode, String geographyCode) throws BTSLBaseException;

	void downloadBulkApprovalFile(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, FileDownloadResponseMulti response, String batchID) throws BTSLBaseException, Exception;

	void bulkUserApproveReject(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, BulkUserApproveRejectResponseVO response,
			String batchID, String batchAction) throws BTSLBaseException, SQLException;

	
	
	//****************bulkUserProcess**********//
	boolean uploadAndValidateFile(Connection con, MComConnectionI mcomCon, String loginID,
			BulkUserProcessRequestVO bulkUserProcessRequestVO, BulkUserProcessResponseVO response, UserVO userVO) throws BTSLBaseException, SQLException;

	BulkUserProcessResponseVO processUploadedFileForBatchApprove(Connection con, MComConnectionI mcomCon, HttpServletResponse response1,
			BulkUserProcessRequestVO bulkUserProcessRequestVO, String filePathAndFileName, String loginID, Locale locale, UserVO userVO, BulkUserProcessResponseVO response) throws BTSLBaseException, SQLException;

	
	

}
