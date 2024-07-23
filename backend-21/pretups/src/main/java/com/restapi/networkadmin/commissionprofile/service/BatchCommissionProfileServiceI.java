package com.restapi.networkadmin.commissionprofile.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnectionI;
import com.restapi.networkadmin.commissionprofile.requestVO.BatchAddCommisionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommProfRespVO;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommisionProfileResponseVO;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;



public interface BatchCommissionProfileServiceI {


	public ArrayList<MasterErrorList> basicFileValidations(BatchAddCommisionProfileRequestVO request, BatchAddCommisionProfileResponseVO response, Locale locale, ArrayList<MasterErrorList> inputValidations) throws BTSLBaseException, Exception;

	public boolean uploadAndValidateFile(Connection con,MComConnectionI mcomCon, String loginId, BatchAddCommisionProfileRequestVO request, BatchAddCommisionProfileResponseVO response,String domainCode,String catrgoryCode) throws BTSLBaseException, SQLException;
	public BatchAddCommisionProfileResponseVO processBulkAddCommissionProf(Connection con,
																		   HttpServletResponse response1, BatchAddCommisionProfileRequestVO request, String p_file, String domainCode,
																		   String catrgoryCode, String batchName, String loginId,Locale locale) throws BTSLBaseException, Exception;
	public BatchAddCommisionProfileResponseVO downloadFileTemplate(Connection con, Locale locale, String loginID, String categoryCode, String domain, HttpServletRequest request, HttpServletResponse responseSwagger) throws BTSLBaseException, SQLException, IOException, ParseException;
	public BatchAddCommisionProfileResponseVO processUploadedFileForCommProfile(Connection con,
																				HttpServletResponse response1, BatchAddCommisionProfileRequestVO request, String p_file, String domainCode,
																				String catrgoryCode, String loginId) throws Exception;

	public BatchAddCommProfRespVO downloadFileTemplateBatchAdd(Connection con, Locale locale, String loginID, String domainCode,String categoryCode, HttpServletRequest request, HttpServletResponse responseSwag)throws BTSLBaseException, SQLException, ParseException, RowsExceededException, WriteException, IOException;
}
