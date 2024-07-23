package com.web.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchUserWebQry {
	
	/**
	 * @return
	 */
	String loadMasterGeographyListQry();
	
	/**
	 * @param con
	 * @param geographicalCode
	 * @param parentGeography
	 * @return
	 * @throws SQLException
	 */
	PreparedStatement addChannelUserListChildGeographyAllowedQry(Connection con, String geographicalCode, String parentGeography) throws SQLException;
	
	/**
	 * @param con
	 * @param geoDomainCode
	 * @param geoTypeSeqNo
	 * @return
	 * @throws SQLException
	 */
	PreparedStatement addChannelUserListSelectParentGeographyQry(Connection con,String geoDomainCode, int geoTypeSeqNo) throws SQLException;
	
	/**
	 * @param domainCode
	 * @param networkCode
	 * @return
	 */
	String loadCommProfileListQry(String domainCode,String networkCode);
	
	/**
	 * @param currentLevel
	 * @return
	 */
	String loadBatchBarListForApprovalQry( String currentLevel);
	

}
