package com.btsl.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class LoanProfileOracleQry implements LoanProfileQry {
	


	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public PreparedStatement addLoanProfileQry(Connection con, LoanProfileCombinedVO loanProfileVO) throws SQLException{
		
		final String METHOD_NAME = "addLoanProfile";
		final StringBuffer sb = new StringBuffer();
		
		sb.append("INSERT INTO LOAN_PROFILES (PROFILE_ID, PROFILE_NAME, CATEGORY_CODE, ");
		sb.append("PROFILE_TYPE, NETWORK_CODE, ");
		sb.append("CREATED_BY, CREATED_ON, MODIFIED_BY, MODIFIED_ON) ");
		sb.append("VALUES (?,?,?,?,?,?,?,?,?)");
		
		final String insertQry = sb.toString();
		
		
		
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Query sqlInsert:" + insertQry);
        }
        

        
        
        int i=1;
        PreparedStatement pstmt = con.prepareStatement(insertQry);
        pstmt.setString(i++, loanProfileVO.getProfileID());
        pstmt.setString(i++, loanProfileVO.getProfileName());
        pstmt.setString(i++, loanProfileVO.getCategoryCode());
        pstmt.setString(i++, loanProfileVO.getProfileType());
        pstmt.setString(i++, loanProfileVO.getNetworkCode());
        pstmt.setString(i++, loanProfileVO.getCreatedBy());
        pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(loanProfileVO.getCreatedOn()));
        pstmt.setString(i++, loanProfileVO.getCreatedBy());
        pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(loanProfileVO.getModifiedOn()));
        
        
        
        
        
		
		
		return pstmt;
	}

	public PreparedStatement addLoanProfileDetailsQry(Connection con, LoanProfileDetailsVO loanProfileDetailsVO, String profileID)
			throws  SQLException, BTSLBaseException {
		
		
		final String METHOD_NAME = "addLoanProfileDetailsQry";
		final StringBuffer sb = new StringBuffer();
		
		sb.append("INSERT INTO LOAN_PROFILE_DETAILS (PROFILE_ID, PRODUCT_CODE, FROM_RANGE, ");
		sb.append("TO_RANGE, INTEREST_TYPE, ");
		sb.append("INTEREST_VALUE) ");
		sb.append("VALUES (?,?,?,?,?,?)");
		
		final String insertQry = sb.toString();
		
		
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Query sqlInsert:" + insertQry);
        }
        
        int i=1;
        PreparedStatement pstmt = con.prepareStatement(insertQry);
        pstmt.setString(i++, profileID);
        pstmt.setString(i++, loanProfileDetailsVO.getProductCode());
        pstmt.setInt(i++, Integer.parseInt(loanProfileDetailsVO.getFromRangeAsString()));
		pstmt.setInt(i++, Integer.parseInt(loanProfileDetailsVO.getToRangeAsString()));
		pstmt.setString(i++, loanProfileDetailsVO.getInterestType());
		//pstmt.setInt(i++, Integer.parseInt(loanProfileDetailsVO.getInterestValueAsString()));
		pstmt.setDouble(i++, PretupsBL.getSystemAmount(Double.parseDouble(loanProfileDetailsVO.getInterestValueAsString())));
		//System.out.println(PretupsBL.getSystemAmount(Double.parseDouble(loanProfileDetailsVO.getInterestValueAsString())));
		
		
		return pstmt;
	}

	public PreparedStatement isProfileNameExistsQry(Connection con, String profileName) throws SQLException{
		final String METHOD_NAME = "isProfileNameExistsQry";
		final StringBuffer sb = new StringBuffer();		
		
		sb.append("SELECT PROFILE_ID, PROFILE_NAME FROM LOAN_PROFILES ");
		sb.append("WHERE PROFILE_NAME = ?  AND STATUS <> 'N' AND STATUS <> 'S' ");
		
		final String selectQry = sb.toString();
		
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Query sqlSelect:" + selectQry);
        }
        
        int i=1;
        PreparedStatement pstmt = con.prepareStatement(selectQry);
		pstmt.setString(i++, profileName);
		
		return pstmt;
	}
	
	public PreparedStatement loadLoanProfilesQry(Connection con, String categoryCode, String networkCode) throws  SQLException {
		final String METHOD_NAME = "loadLoanProfilesQry";
		final StringBuffer sb = new StringBuffer();	
		
		
		sb.append("SELECT PROFILE_ID, PROFILE_NAME, CATEGORY_CODE,");
		sb.append(" PROFILE_TYPE, NETWORK_CODE, STATUS");
		sb.append(" FROM LOAN_PROFILES");
		sb.append(" WHERE CATEGORY_CODE=? AND NETWORK_CODE=? AND STATUS=? ");
		
		final String selectQry = sb.toString();
		
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Query sqlSelect:" + selectQry);
        }
		int i=1;
		PreparedStatement pstmt = con.prepareStatement(selectQry);
		pstmt.setString(i++, categoryCode);
		pstmt.setString(i++, networkCode);
		pstmt.setString(i++, PretupsI.YES);
		
		
		return pstmt;
	}

	public PreparedStatement loadLoanProfileSlabsQry(Connection con, String profileID) throws SQLException {
		
		final String METHOD_NAME = "loadLoanProfileSlabsQry";
		final StringBuffer sb = new StringBuffer();	
		
		
		sb.append("SELECT LPD.PROFILE_ID, LPD.PRODUCT_CODE, LPD.FROM_RANGE, LPD.TO_RANGE,");
		sb.append(" LPD.INTEREST_TYPE, LPD.INTEREST_VALUE,LP.PROFILE_TYPE ");
		sb.append(" FROM LOAN_PROFILE_DETAILS LPD ,LOAN_PROFILES LP ");
		sb.append(" WHERE  LP.PROFILE_ID=LPD.PROFILE_ID AND LP.PROFILE_ID =? AND LP.STATUS = ? ORDER BY LPD.FROM_RANGE ASC");
		
		final String selectQry = sb.toString();
		
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Query sqlSelect:" + selectQry);
        }
		int i=1;
		PreparedStatement pstmt = con.prepareStatement(selectQry);
		pstmt.setString(i++, profileID);
		pstmt.setString(i++, PretupsI.YES);
		
		
		return pstmt;
	}

	public PreparedStatement loadLoanProfileByIdQry(Connection con, String profileID) throws SQLException{
		final String METHOD_NAME = "loadLoanProfileByIdQry";
		final StringBuffer sb = new StringBuffer();	
		
		sb.append("SELECT PROFILE_ID, PROFILE_NAME, PROFILE_TYPE");
		sb.append(" FROM LOAN_PROFILES WHERE  PROFILE_ID=? AND STATUS = ? ");
		
		
		final String selectQry = sb.toString();
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Query sqlSelect:" + selectQry);
        }
        
        int i=1;
        PreparedStatement pstmt = con.prepareStatement(selectQry);
        pstmt.setString(i++, profileID);
        pstmt.setString(i++, PretupsI.YES);
		
        return pstmt;
	}

	public PreparedStatement updateLoanProfileQry(Connection con, LoanProfileCombinedVO loanProfileVO) throws SQLException{
		final String METHOD_NAME = "updateLoanProfileQry";
		final StringBuffer sb = new StringBuffer();	
		
		sb.append("UPDATE LOAN_PROFILES SET PROFILE_NAME=?, PROFILE_TYPE=?,");
		sb.append(" MODIFIED_BY=?, MODIFIED_ON=? ");
		sb.append(" WHERE  PROFILE_ID=?  AND STATUS = ?");
		
		
		final String updateQry = sb.toString();
	    if (log.isDebugEnabled()) {
	        log.debug(METHOD_NAME, "Query sqlUpdate:" + updateQry);
	    }
	    
	    int i=1;
	    PreparedStatement pstmt = con.prepareStatement(updateQry);
	    pstmt.setString(i++, loanProfileVO.getProfileName());
	    pstmt.setString(i++, loanProfileVO.getProfileType());
	    pstmt.setString(i++, loanProfileVO.getModifiedBy());
	    pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(loanProfileVO.getModifiedOn()));
		pstmt.setString(i++, loanProfileVO.getProfileID());
		pstmt.setString(i++, PretupsI.YES);
		
		return pstmt;
	}

	public PreparedStatement deleteLoanSlabsById(Connection con, String profileID) throws SQLException {
		
		final String METHOD_NAME = "deleteLoanSlabsById";
		final StringBuffer sb = new StringBuffer();	
		
		
		sb.append("DELETE FROM LOAN_PROFILE_DETAILS WHERE PROFILE_ID=? ");
		
		final String deleteQry = sb.toString();
		
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Query sqlDelete:" + deleteQry);
        }
		int i=1;
		PreparedStatement pstmt = con.prepareStatement(deleteQry);
		pstmt.setString(i++, profileID);
		
		
		return pstmt;
	}

	public PreparedStatement deleteLoanProfileQry(Connection con, String profileID) throws SQLException {
		final String METHOD_NAME = "deleteLoanProfileQry";
		final StringBuffer sb = new StringBuffer();	
		
		
		sb.append("DELETE FROM LOAN_PROFILES WHERE PROFILE_ID=? ");
		
		final String deleteQry = sb.toString();
		
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Query sqlDelete:" + deleteQry);
        }
		int i=1;
		PreparedStatement pstmt = con.prepareStatement(deleteQry);
		pstmt.setString(i++, profileID);
		
		
		return pstmt;
	}

	public PreparedStatement isProfileAssociatedQry(Connection con, String profileID) throws SQLException {
		final String METHOD_NAME = "isProfileAssociatedQry";
		final StringBuffer sb = new StringBuffer();	
		
		
		sb.append("SELECT 1 FROM CHANNEL_USER_LOAN_INFO WHERE PROFILE_ID=? ");
		
		final String selectQry = sb.toString();
		
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Query sqlDelete:" + selectQry);
        }
		int i=1;
		PreparedStatement pstmt = con.prepareStatement(selectQry);
		pstmt.setString(i++, profileID);
		
		
		return pstmt;
		
	}
	
	
	
	
	
	
	
}
