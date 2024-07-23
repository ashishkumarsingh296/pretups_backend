package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.transfer.businesslogic.errorfilerequest.ErrorFileRequestVO;
import com.btsl.pretups.transfer.businesslogic.errorfileresponse.ErrorFileResponse;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.FileDownloadResponseMulti;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 
 * @author anshul.goyal2
 *
 */
//@Service
	@Component
public interface DownloadUserListService {
	/**
	 * 
	 * @param loginId
	 * @param userCategoryName
	 * @param operationType
	 * @param p_locale
	 * @param response
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void downloadC2CBatch(String loginId, String userCategoryName, String operationType,Locale p_locale,FileDownloadResponseMulti response ) throws BTSLBaseException, SQLException, IOException;
	
	/**
	 * 
	 * @param loginId
	 * @param locale
	 * @param fileDownloadResponse
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void downloadCustomerRechargeList(String loginId, Locale locale, FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, SQLException, IOException;
	
	/**
	 * 
	 * @param loginId
	 * @param locale
	 * @param fileDownloadResponse
	 * @param rerquestMap
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void downloadO2CPurchaseUserList(String loginId, Locale locale, FileDownloadResponseMulti fileDownloadResponse, HashMap<String, String> rerquestMap) throws BTSLBaseException, SQLException, IOException;
	
	/**
	 * 
	 * @param loginId
	 * @param locale
	 * @param fileDownloadResponse
	 * @param rerquestMap
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void downloadO2CWithdrawUserList(String loginId, Locale locale, FileDownloadResponseMulti fileDownloadResponse, HashMap<String, String> rerquestMap) throws BTSLBaseException, SQLException, IOException;
	
	/**
	 * 
	 * @param errorFileRequestVO
	 * @param errorFileResponse
	 * @param responseSwag
	 * @throws BTSLBaseException
	 */
	public void downloadErrorFile(ErrorFileRequestVO errorFileRequestVO, ErrorFileResponse errorFileResponse, HttpServletResponse responseSwag)throws BTSLBaseException;
}
