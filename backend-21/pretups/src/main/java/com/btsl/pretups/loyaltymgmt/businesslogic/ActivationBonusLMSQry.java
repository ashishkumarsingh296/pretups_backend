package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface ActivationBonusLMSQry {
	 Log log = LogFactory.getLog(ActivationBonusLMSQry.class.getName());
	public PreparedStatement viewRedemptionenquiryDetailsQry(Connection con,String pDomainCode,String pCategoryCode,String pUserId,String pZoneCode,String pFromdate,String pTodate) throws SQLException ,ParseException;
	public String loadServicesListQry(String pCatCode);
	public PreparedStatement validateUserdetailsQry(Connection con,String pDomainCode,String pCategoryCode,String pUserIdLoggedUser,String pZoneCode,String pChannelUser) throws SQLException;
	public PreparedStatement loadBonusPointDetailsQry(Connection con,String pDomainCode,String pCategoryCode,String pUserId,String pZoneCode,String pFromdate,String pTodate)throws SQLException ,ParseException;
	public String loadpromotionListQry();
	public String isprofileExpiredQry();
	public PreparedStatement loadMapForLMSAssociationQry(Connection con,String gradeCode,String pCategoryCode,String pUserId,String pGeographyCode) throws SQLException;

}