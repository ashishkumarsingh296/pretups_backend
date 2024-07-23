package com.restapi.o2c.service;

import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.user.service.FileDownloadResponse;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@Service
public interface FocBatchInitiateServiceI {

	/**
	 * 
	 * @param requestVO
	 * @param serviceKeyword
	 * @param calculatorI
	 * @param requestIDStr
	 * @param con
	 * @param locale
	 * @param httprequest
	 * @param headers
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 */
	public FOCBatchTransferResponse processRequest(FOCBatchTransferRequestVO requestVO, String serviceKeyword, OperatorUtilI calculatorI ,String requestIDStr,Connection con , Locale locale,HttpServletRequest httprequest, MultiValueMap<String, String> headers, HttpServletResponse responseSwag)  throws BTSLBaseException;
	
	/**
	 * 
	 * @param con
	 * @param batchFOCFileDownloadRequestVO
	 * @return
	 * @throws BTSLBaseException
	 * @throws RowsExceededException
	 * @throws WriteException
	 * @throws IOException
	 * @throws Exception
	 */
	public FileDownloadResponse userListDownload(Connection con, BatchFOCFileDownloadRequestVO batchFOCFileDownloadRequestVO, UserVO userVO)  throws Exception;

}
