package com.restapi.channelAdmin.service;

import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.user.businesslogic.OAuthUser;
import com.restapi.channelAdmin.requestVO.UserMigrationRequestVO;
import com.restapi.channelAdmin.responseVO.UserMigrationResponseVO;
import com.restapi.user.service.FileDownloadResponse;

public interface UserMovementService {

	/**
	 * 
	 * @param oAuthUser
	 * @param responseSwag
	 * @param locale
	 * @param domainCode 
	 * @return
	 * @throws SQLException 
	 */
	FileDownloadResponse getUserMovementTemplate(OAuthUser oAuthUser, HttpServletResponse responseSwag, Locale locale, String domainCode) throws SQLException;

	UserMigrationResponseVO confirmUserMigration(Locale locale, UserMigrationRequestVO requestVO, HttpServletResponse responseSwag,
			OAuthUser oAuthUser,String domainCode)throws SQLException ;

	FileDownloadResponse getNpUserList(OAuthUser oAuthUser, HttpServletResponse responseSwag, Locale locale,String domain)throws SQLException;
	

}
