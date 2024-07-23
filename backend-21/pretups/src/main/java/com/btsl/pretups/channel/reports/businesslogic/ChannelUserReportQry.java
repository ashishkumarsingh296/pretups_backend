package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public interface ChannelUserReportQry {
	
	public PreparedStatement loadUserListBasisOfZoneDomainCategoryQry(Connection pCon,String pUserCategory,String pDomainCode,String pZoneCode,String pUserID,String pUserName) throws SQLException ;

	public PreparedStatement loadUserListBasisOfZoneDomainCategoryHierarchyQry(Connection pCon,String pUserCategory,String pDomainCode,String pZoneCode,String pUserID,String pUserName) throws SQLException ;

	public PreparedStatement loadUserListOnZoneDomainCategoryQry(String pFromUserID, String pUserName,Connection pCon,String pUserCategory,String domainCode,String ploginuserID,String pZoneCode )throws SQLException;
	
	
	public PreparedStatement loadUserListWithOwnerIDOnZoneDomainCategoryQry(Connection pCon,String pUserCategory,String pDomainCode,String pZoneCode,String ploginuserID,String pFromUserID, String pUserName)throws SQLException ;

	
	public PreparedStatement loadUserListOnZoneCategoryHierarchyQry(Connection pCon,String pUserCategory,String domainCode,String pZoneCode,String ploginuserID,String pUserName) throws SQLException;

	public PreparedStatement loadUserListWithOwnerIDOnZoneCategoryHierarchy(Connection pCon,String pUserCategory,String domainCode,String pZoneCode,String ploginuserID,String pUserName) throws SQLException;

	public StringBuffer prepareUserGeographyQuery();
}
