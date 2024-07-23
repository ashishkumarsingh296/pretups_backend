package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.web.pretups.channel.reports.web.LmsRedemptionReportModel;

/**
 * 
 * @author sweta.verma
 *
 */
public interface LmsRedemptionRetWidRptQry {
	/**
	 * 
	 * @param pCon
	 * @param lmsRedemptionReportModel
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public PreparedStatement loadRedemptionRetWidQry(Connection pCon,LmsRedemptionReportModel lmsRedemptionReportModel) throws SQLException, ParseException ;
	
	
}
