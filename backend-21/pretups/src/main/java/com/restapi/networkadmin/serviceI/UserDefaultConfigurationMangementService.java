package com.restapi.networkadmin.serviceI;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.UserDefaultConfigurationTemplateFileRequestVO;
import com.restapi.networkadmin.responseVO.CategoryDomainListResponseVO;
import com.restapi.networkadmin.responseVO.UserDefaultConfigMangementRespVO;
import com.restapi.networkadmin.responseVO.UserDefaultConfigmgmntFileResponseVO;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@Service
public interface UserDefaultConfigurationMangementService {

	/**
	 * 
	 * @param con
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public UserDefaultConfigMangementRespVO downloadTemplateFileForSeletedDomain(Connection con,Locale locale,String domainCode,String loginUserID,HttpServletRequest request ,HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException, ParseException, RowsExceededException, WriteException, IOException;
	
	public UserDefaultConfigmgmntFileResponseVO processUploadeFile(Connection con, HttpServletResponse response1,
			String loginID, String domainCode,
			UserDefaultConfigurationTemplateFileRequestVO request) throws BTSLBaseException, SQLException,IOException;
	
	
	public ArrayList<MasterErrorList> basicFileValidations(UserDefaultConfigurationTemplateFileRequestVO request, UserDefaultConfigmgmntFileResponseVO response, Locale locale, ArrayList<MasterErrorList> inputValidations) throws BTSLBaseException;

	public boolean uploadAndValidateFile(Connection con,MComConnectionI mcomCon, String loginId, UserDefaultConfigurationTemplateFileRequestVO request, UserDefaultConfigmgmntFileResponseVO response,String domainCode) throws BTSLBaseException, SQLException;

	CategoryDomainListResponseVO loadDomainListForOperator(Connection con, Locale locale, HttpServletResponse response1, UserVO userVO,
			CategoryDomainListResponseVO response);
	
}
